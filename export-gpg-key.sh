#!/bin/bash

# Export GPG key for GitHub Secrets
# Usage: ./export-gpg-key.sh BC4E150B189FE436

KEY_ID="${1:-BC4E150B189FE436}"

echo "Exporting GPG key: $KEY_ID"
echo "================================"

# Export the key with armor, then base64 encode it
gpg --armor --export-secret-keys "$KEY_ID" | base64 | tr -d '\n' > gpg-key-base64.txt

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

