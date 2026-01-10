#!/bin/bash

# Export GPG key for GitHub Secrets
# Usage: ./export-gpg-key.sh [KEY_ID]

if [ -z "$1" ]; then
    echo "Available GPG secret keys:"
    echo ""
    gpg --list-secret-keys --keyid-format LONG
    echo ""
    read -p "Enter your GPG Key ID: " KEY_ID
else
    KEY_ID="$1"
fi

if [ -z "$KEY_ID" ]; then
    echo "Error: KEY_ID is required"
    exit 1
fi

echo "Exporting GPG key: $KEY_ID"
echo "================================"

# Export the key with armor, then base64 encode it
gpg --armor --export-secret-keys "$KEY_ID" | base64 | tr -d '\n' > gpg-key-base64.txt

if [ $? -eq 0 ] && [ -s gpg-key-base64.txt ]; then
    echo ""
    echo "✓ GPG key exported to: gpg-key-base64.txt"
    echo ""
    echo "Copy the contents of gpg-key-base64.txt and use it as the GPG_PRIVATE_KEY secret in GitHub"
    echo ""
    echo "To view the key:"
    echo "  cat gpg-key-base64.txt"
    echo ""
    echo "To copy to clipboard (macOS):"
    echo "  cat gpg-key-base64.txt | pbcopy"
    echo ""
    echo "⚠️  Remember to delete this file after copying:"
    echo "  rm gpg-key-base64.txt"
    echo ""
else
    echo ""
    echo "❌ Failed to export GPG key"
    echo "Make sure the key ID is correct:"
    echo "  gpg --list-secret-keys"
    echo ""
    rm -f gpg-key-base64.txt
    exit 1
fi

