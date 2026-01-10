# GitHub Secrets Configuration

## Required Secrets

Configure these secrets in your GitHub repository:
**Settings → Secrets and variables → Actions → New repository secret**

### 1. SONATYPE_USERNAME
Your Sonatype Central Portal username (usually your email: `pixamob@gmail.com`)

### 2. SONATYPE_PASSWORD
Your Sonatype Central Portal password or generated token

### 3. GPG_PRIVATE_KEY
Your GPG private key in base64 format (see instructions below)

### 4. GPG_PASSPHRASE
```
khraila3ra/@2EWQ1992Xpgxa
```

---

## How to Export Your GPG Private Key

Run this command to export your GPG key in the correct format for GitHub Secrets:

```bash
gpg --armor --export-secret-keys BC4E150B189FE436 | base64 | pbcopy
```

This will:
1. Export your GPG private key with ASCII armor
2. Encode it in base64
3. Copy it to your clipboard (macOS)

**For Linux users:**
```bash
gpg --armor --export-secret-keys BC4E150B189FE436 | base64 -w 0 | xclip -selection clipboard
```

**Alternative (save to file):**
```bash
gpg --armor --export-secret-keys BC4E150B189FE436 | base64 > gpg-key-base64.txt
cat gpg-key-base64.txt  # View the content
pbcopy < gpg-key-base64.txt  # Copy to clipboard
```

⚠️ **Important:** The base64 string should include the `-----BEGIN PGP PRIVATE KEY BLOCK-----` and `-----END PGP PRIVATE KEY BLOCK-----` markers encoded within it.

---

## How to Set Up Secrets

1. Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions
2. Click "New repository secret"
3. Add each secret one by one with the exact name and value:
   - `SONATYPE_USERNAME` - Your Sonatype account email
   - `SONATYPE_PASSWORD` - Your Sonatype password or token
   - `GPG_PRIVATE_KEY` - The base64-encoded key from the command above
   - `GPG_PASSPHRASE` - Your GPG key passphrase: `khraila3ra/@2EWQ1992Xpgxa`
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

If the GitHub Action fails with "error decoding base64 input stream":
1. Make sure you used `base64` encoding (not just copying the armored key)
2. Verify the key includes BEGIN/END markers
3. Check there are no extra newlines or spaces

To test locally:
```bash
echo "$YOUR_BASE64_KEY" | base64 -d | gpg --list-packets
```

This should show the GPG key packets without errors.

