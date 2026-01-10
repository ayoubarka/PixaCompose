#!/bin/bash

# GPG Key Setup Script for PixaCompose Publishing
# This script helps you generate and export GPG keys for Maven Central publishing

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  GPG Key Setup for Maven Central${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Check if GPG is installed
if ! command -v gpg &> /dev/null; then
    echo -e "${RED}ERROR: GPG is not installed${NC}"
    echo ""
    echo "Install GPG:"
    echo "  macOS:   brew install gnupg"
    echo "  Linux:   apt-get install gnupg"
    exit 1
fi

echo -e "${GREEN}✓ GPG is installed${NC}"
echo ""

# Check for existing keys
echo -e "${YELLOW}Checking for existing GPG keys...${NC}"
if gpg --list-secret-keys --keyid-format=long | grep -q "sec"; then
    echo -e "${GREEN}Found existing GPG keys:${NC}"
    gpg --list-secret-keys --keyid-format=long
    echo ""

    read -p "Do you want to use an existing key? (y/n): " -n 1 -r
    echo ""

    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo ""
        echo "Enter your key ID (8 hex characters after rsa4096/, e.g., ABCD1234EFGH5678):"
        read KEY_ID
    else
        KEY_ID=""
    fi
else
    echo -e "${YELLOW}No existing GPG keys found${NC}"
    KEY_ID=""
fi

# Generate new key if needed
if [ -z "$KEY_ID" ]; then
    echo ""
    echo -e "${YELLOW}Generating new GPG key...${NC}"
    echo ""
    echo "You will be prompted for:"
    echo "  1. Key type: Choose (1) RSA and RSA"
    echo "  2. Key size: Enter 4096"
    echo "  3. Expiration: Enter 0 (never expires) or set as desired"
    echo "  4. Name: Enter 'Ayoub Arka'"
    echo "  5. Email: Enter 'ayoub@pixamob.com'"
    echo "  6. Passphrase: Choose a STRONG passphrase and REMEMBER IT!"
    echo ""
    read -p "Press Enter to continue..."

    gpg --full-generate-key

    echo ""
    echo -e "${GREEN}✓ GPG key generated${NC}"
    echo ""

    # Get the key ID
    echo "Your GPG keys:"
    gpg --list-secret-keys --keyid-format=long
    echo ""
    echo "Enter your new key ID (8 hex characters after rsa4096/, e.g., ABCD1234EFGH5678):"
    read KEY_ID
fi

# Validate key ID
if ! gpg --list-secret-keys --keyid-format=long | grep -q "$KEY_ID"; then
    echo -e "${RED}ERROR: Key ID not found${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}✓ Using key: $KEY_ID${NC}"
echo ""

# Publish public key to keyservers
echo -e "${YELLOW}Publishing public key to keyservers...${NC}"

gpg --keyserver keyserver.ubuntu.com --send-keys "$KEY_ID" 2>/dev/null || echo "  Note: keyserver.ubuntu.com might be slow"
gpg --keyserver keys.openpgp.org --send-keys "$KEY_ID" 2>/dev/null || echo "  Note: keys.openpgp.org might require email verification"
gpg --keyserver pgp.mit.edu --send-keys "$KEY_ID" 2>/dev/null || echo "  Note: pgp.mit.edu might be unreachable"

echo -e "${GREEN}✓ Public key published${NC}"
echo ""

# Export private key for GitHub Secret
echo -e "${YELLOW}Exporting private key for GitHub Secret...${NC}"

GPG_KEY_FILE="$HOME/gpg-private-key-base64.txt"
gpg --export-secret-keys "$KEY_ID" | base64 > "$GPG_KEY_FILE"

echo -e "${GREEN}✓ Private key exported to: $GPG_KEY_FILE${NC}"
echo ""

# Export for local Gradle signing
echo -e "${YELLOW}Exporting secret keyring for local Gradle...${NC}"

SECRING_FILE="$HOME/.gnupg/secring.gpg"
gpg --export-secret-keys "$KEY_ID" > "$SECRING_FILE"

echo -e "${GREEN}✓ Secret keyring created: $SECRING_FILE${NC}"
echo ""

# Display key ID for reference
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}SUCCESS! GPG Key Setup Complete${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${YELLOW}Your GPG Key ID (last 8 characters):${NC}"
echo -e "${GREEN}$KEY_ID${NC}"
echo ""

echo -e "${YELLOW}GitHub Secret - GPG_PRIVATE_KEY:${NC}"
echo "File: $GPG_KEY_FILE"
echo "To copy to clipboard (macOS):"
echo "  cat $GPG_KEY_FILE | pbcopy"
echo ""
echo "To view:"
echo "  cat $GPG_KEY_FILE"
echo ""

# Show passphrase reminder
echo -e "${YELLOW}GitHub Secret - GPG_PASSPHRASE:${NC}"
echo "This is the passphrase you entered when creating the key."
echo -e "${RED}Make sure you remember it!${NC}"
echo ""

# Show local Gradle configuration
echo -e "${YELLOW}Local Gradle Configuration:${NC}"
echo "Add to ~/.gradle/gradle.properties:"
echo ""
echo "signing.keyId=$KEY_ID"
echo "signing.password=YOUR_GPG_PASSPHRASE"
echo "signing.secretKeyRingFile=$SECRING_FILE"
echo ""

# Offer to create/update gradle.properties
read -p "Do you want to update ~/.gradle/gradle.properties now? (y/n): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    GRADLE_PROPS="$HOME/.gradle/gradle.properties"

    # Create directory if it doesn't exist
    mkdir -p "$HOME/.gradle"

    # Backup existing file
    if [ -f "$GRADLE_PROPS" ]; then
        cp "$GRADLE_PROPS" "$GRADLE_PROPS.backup"
        echo -e "${GREEN}✓ Backed up existing file to $GRADLE_PROPS.backup${NC}"
    fi

    # Append or create
    echo "" >> "$GRADLE_PROPS"
    echo "# GPG Signing Configuration for PixaCompose" >> "$GRADLE_PROPS"
    echo "signing.keyId=$KEY_ID" >> "$GRADLE_PROPS"
    echo "signing.password=YOUR_GPG_PASSPHRASE_HERE" >> "$GRADLE_PROPS"
    echo "signing.secretKeyRingFile=$SECRING_FILE" >> "$GRADLE_PROPS"

    echo -e "${GREEN}✓ Updated $GRADLE_PROPS${NC}"
    echo -e "${YELLOW}⚠️  IMPORTANT: Edit the file and replace YOUR_GPG_PASSPHRASE_HERE with your actual passphrase${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${YELLOW}Next Steps:${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo "1. Copy GPG_PRIVATE_KEY value:"
echo "   cat $GPG_KEY_FILE | pbcopy  # macOS"
echo ""
echo "2. Go to GitHub:"
echo "   https://github.com/ayoubarka/PixaCompose/settings/secrets/actions"
echo ""
echo "3. Add 4 secrets:"
echo "   - GPG_PRIVATE_KEY (paste from clipboard)"
echo "   - GPG_PASSPHRASE (your GPG passphrase)"
echo "   - SONATYPE_USERNAME (from https://central.sonatype.com/)"
echo "   - SONATYPE_PASSWORD (from https://central.sonatype.com/)"
echo ""
echo "4. Re-run GitHub Actions workflow or push a new tag"
echo ""
echo "For detailed instructions, see: GITHUB_SECRETS_SETUP.md"
echo ""

