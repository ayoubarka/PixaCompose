#!/bin/bash

# Setup GitHub Secrets for PixaCompose Publishing
# This script helps you configure all required secrets

set -e

REPO="ayoubarka/PixaCompose"

echo "================================================"
echo "  PixaCompose - GitHub Secrets Setup Helper"
echo "================================================"
echo ""
echo "This script will help you configure GitHub secrets for publishing."
echo "You'll need the GitHub CLI (gh) installed and authenticated."
echo ""

# Check if gh is installed
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) is not installed."
    echo ""
    echo "Install it with:"
    echo "  brew install gh"
    echo ""
    echo "Then authenticate with:"
    echo "  gh auth login"
    echo ""
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo "❌ You are not authenticated with GitHub CLI."
    echo ""
    echo "Run:"
    echo "  gh auth login"
    echo ""
    exit 1
fi

echo "✓ GitHub CLI is installed and authenticated"
echo ""

# List GPG keys
echo "================================================"
echo "Step 1: Select GPG Key"
echo "================================================"
echo ""
echo "Available GPG secret keys:"
gpg --list-secret-keys --keyid-format LONG
echo ""
read -p "Enter your GPG Key ID (e.g., the 16-character ID): " GPG_KEY_ID

if [ -z "$GPG_KEY_ID" ]; then
    echo "❌ GPG Key ID is required"
    exit 1
fi

# Export GPG key
echo ""
echo "================================================"
echo "Step 2: Exporting GPG Private Key"
echo "================================================"
echo ""
echo "Exporting GPG key $GPG_KEY_ID..."
GPG_PRIVATE_KEY=$(gpg --armor --export-secret-keys "$GPG_KEY_ID" | base64 | tr -d '\n')

if [ -z "$GPG_PRIVATE_KEY" ]; then
    echo "❌ Failed to export GPG key"
    echo ""
    echo "Make sure the key exists:"
    echo "  gpg --list-secret-keys $GPG_KEY_ID"
    echo ""
    exit 1
fi

echo "✓ GPG key exported successfully"
echo ""

# Prompt for secrets
echo "================================================"
echo "Step 3: Enter Secret Values"
echo "================================================"
echo ""

read -p "Enter SONATYPE_USERNAME (email): " SONATYPE_USERNAME
read -sp "Enter SONATYPE_PASSWORD: " SONATYPE_PASSWORD
echo ""
read -sp "Enter GPG_PASSPHRASE: " GPG_PASSPHRASE
echo ""
echo ""

# Confirm
echo "================================================"
echo "Step 4: Confirm Configuration"
echo "================================================"
echo ""
echo "Repository: $REPO"
echo "SONATYPE_USERNAME: $SONATYPE_USERNAME"
echo "SONATYPE_PASSWORD: ********"
echo "GPG_PASSPHRASE: ********"
echo "GPG_PRIVATE_KEY: <exported>"
echo ""
read -p "Continue? (y/N): " CONFIRM

if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
    echo "Aborted."
    exit 0
fi

# Set secrets
echo ""
echo "================================================"
echo "Step 5: Setting GitHub Secrets"
echo "================================================"
echo ""

echo "Setting SONATYPE_USERNAME..."
echo "$SONATYPE_USERNAME" | gh secret set SONATYPE_USERNAME -R "$REPO"

echo "Setting SONATYPE_PASSWORD..."
echo "$SONATYPE_PASSWORD" | gh secret set SONATYPE_PASSWORD -R "$REPO"

echo "Setting GPG_PASSPHRASE..."
echo "$GPG_PASSPHRASE" | gh secret set GPG_PASSPHRASE -R "$REPO"

echo "Setting GPG_PRIVATE_KEY..."
echo "$GPG_PRIVATE_KEY" | gh secret set GPG_PRIVATE_KEY -R "$REPO"

echo ""
echo "================================================"
echo "✅ All secrets configured successfully!"
echo "================================================"
echo ""
echo "Next steps:"
echo "1. Create a release tag: git tag -a v1.0.0 -m 'Release 1.0.0'"
echo "2. Push the tag: git push origin v1.0.0"
echo "3. Monitor the workflow: https://github.com/$REPO/actions"
echo ""

