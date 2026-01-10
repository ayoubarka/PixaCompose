# GitHub Secrets Configuration

## Required Secrets

Configure these secrets in your GitHub repository:
**Settings → Secrets and variables → Actions → New repository secret**

### 1. SONATYPE_USERNAME
Your Sonatype Central Portal username (usually your email)

Example: `your-email@example.com`

### 2. SONATYPE_PASSWORD
Your Sonatype Central Portal password or generated token

⚠️ Never commit this value to Git!

### 3. GPG_PRIVATE_KEY
Your GPG private key in base64 format (see instructions below)

⚠️ This is sensitive! Never commit the actual key to Git!

### 4. GPG_PASSPHRASE
Your GPG key passphrase

⚠️ Never commit this value to Git!

---

## How to Export Your GPG Private Key

Run this command to export your GPG key in the correct format for GitHub Secrets:

```bash
# Replace YOUR_KEY_ID with your actual GPG key ID
gpg --armor --export-secret-keys YOUR_KEY_ID | base64 | pbcopy
```

This will:
1. Export your GPG private key with ASCII armor
2. Encode it in base64
3. Copy it to your clipboard (macOS)

**For Linux users:**
```bash
gpg --armor --export-secret-keys YOUR_KEY_ID | base64 -w 0 | xclip -selection clipboard
```

**Alternative (save to file):**
```bash
gpg --armor --export-secret-keys YOUR_KEY_ID | base64 > gpg-key-base64.txt
cat gpg-key-base64.txt  # View the content
pbcopy < gpg-key-base64.txt  # Copy to clipboard
rm gpg-key-base64.txt  # Delete after use!
```

⚠️ **Important:** 
- The base64 string should include the `-----BEGIN PGP PRIVATE KEY BLOCK-----` and `-----END PGP PRIVATE KEY BLOCK-----` markers encoded within it.
- Never commit the exported key file to Git
- Delete the key file after copying it to GitHub Secrets

---

## How to Set Up Secrets

### Option A: Using the Setup Script (Recommended)

```bash
./setup-github-secrets.sh
```

This automated script will:
- Export your GPG key
- Prompt for credentials
- Set all GitHub secrets automatically

Requires: GitHub CLI (`gh`) installed and authenticated

### Option B: Manual Setup via Web Interface

1. Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions
2. Click "New repository secret"
3. Add each secret one by one with the exact name and value:
   - `SONATYPE_USERNAME` - Your Sonatype account email
   - `SONATYPE_PASSWORD` - Your Sonatype password or token
   - `GPG_PRIVATE_KEY` - The base64-encoded key from the export command
   - `GPG_PASSPHRASE` - Your GPG key passphrase
4. After all 4 secrets are configured, the GitHub Action will work correctly

---

## Verify Setup

After setting up secrets, test by creating a tag:

```bash
git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin v1.0.1
```

The GitHub Action will automatically trigger and publish to Maven Central.

---

## Troubleshooting

### If the GitHub Action fails with "error decoding base64 input stream":
1. Make sure you used `base64` encoding (not just copying the armored key)
2. Verify the key includes BEGIN/END markers
3. Check there are no extra newlines or spaces

To test locally:
```bash
echo "$YOUR_BASE64_KEY" | base64 -d | gpg --list-packets
```

This should show the GPG key packets without errors.

### If you see "401 Unauthorized":
- Verify your Sonatype credentials are correct
- Try generating a new token in Sonatype Central Portal

### If you see signing errors:
- Verify GPG_PRIVATE_KEY is correctly base64-encoded
- Verify GPG_PASSPHRASE matches your key's passphrase

---

## Security Best Practices

✅ **DO:**
- Store secrets only in GitHub Secrets (encrypted)
- Use the automated setup script
- Delete local key files after use
- Rotate credentials periodically

❌ **DON'T:**
- Commit secrets to Git (even in documentation)
- Share secrets in plain text
- Store secrets in environment variables permanently
- Commit `gradle.properties` with real credentials

---

## Additional Resources

- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [Sonatype Central Portal Guide](https://central.sonatype.org/publish/publish-portal-gradle/)
- [GPG Key Management](https://docs.github.com/en/authentication/managing-commit-signature-verification)


