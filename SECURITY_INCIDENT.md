# Security Incident Response - GPG Key Exposure

## What Happened

GitGuardian detected a Base64 PGP Private Key exposed in the GitHub repository on January 10, 2026 at 20:15:47 UTC.

## Immediate Actions Taken

✅ **1. Removed Exposed Credentials**
- Removed hardcoded GPG key from GITHUB_SECRETS.md
- Removed passphrase from all documentation files
- Removed email addresses from documentation
- Removed GPG key ID references

✅ **2. Updated .gitignore**
- Added `GITHUB_SECRETS.md` to .gitignore
- Added `gpg-key-base64.txt` to .gitignore
- Added `private.key` to .gitignore
- Added pattern for any secrets*.md files

✅ **3. Updated Scripts**
- Modified `setup-github-secrets.sh` to prompt for key ID
- Modified `export-gpg-key.sh` to prompt for key ID
- Added warnings about deleting key files after use

✅ **4. Cleaned Local Files**
- Removed temporary key files from workspace
- Ensured no sensitive files are tracked

## Next Steps Required

### 1. Revoke the Exposed GPG Key (CRITICAL)

```bash
# List your keys to find the exposed one
gpg --list-secret-keys --keyid-format LONG

# Revoke the compromised key
gpg --gen-revoke YOUR_KEY_ID > revoke.asc

# Import the revocation certificate
gpg --import revoke.asc

# Publish the revocation to key servers
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID

# Delete the local key
gpg --delete-secret-keys YOUR_KEY_ID
gpg --delete-keys YOUR_KEY_ID

# Clean up
rm revoke.asc
```

### 2. Generate a New GPG Key

```bash
# Generate new key
gpg --full-generate-key

# Choose:
# - Key type: RSA and RSA
# - Key size: 4096
# - Expiration: 2 years (recommended)
# - Real name: Ayoub Oubarka
# - Email: your-email@example.com
# - Passphrase: Use a strong, unique passphrase

# Get the new key ID
gpg --list-secret-keys --keyid-format LONG

# Export public key to GitHub
gpg --armor --export NEW_KEY_ID
# Copy this and add to GitHub: https://github.com/settings/keys
```

### 3. Update GitHub Secrets

```bash
# Use the setup script with the NEW key ID
cd /Users/ayouboubarka/StudioProjects/PixaCompose
./setup-github-secrets.sh
```

Or manually:
1. Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions
2. Update `GPG_PRIVATE_KEY` with the new key
3. Update `GPG_PASSPHRASE` with the new passphrase

### 4. Clean Git History (Optional but Recommended)

The exposed key is still in Git history. To completely remove it:

```bash
# Install BFG Repo-Cleaner
brew install bfg

# Clone a fresh copy
cd /tmp
git clone --mirror https://github.com/ayoubarka/PixaCompose.git

# Remove the exposed credentials from history
cd PixaCompose.git
bfg --replace-text <(echo "khraila3ra/@2EWQ1992Xpgxa==>REDACTED")
bfg --delete-files "GITHUB_SECRETS.md"

# Clean up
git reflog expire --expire=now --all
git gc --prune=now --aggressive

# Force push (WARNING: This rewrites history)
git push --force

# Update your local repo
cd /Users/ayouboubarka/StudioProjects/PixaCompose
git pull --rebase
```

**⚠️ Warning:** Rewriting history affects all collaborators. Use with caution.

### 5. Update Sonatype Password (Recommended)

If your Sonatype password was also exposed:

1. Login to: https://central.sonatype.com/
2. Go to Account Settings
3. Change your password
4. Update the `SONATYPE_PASSWORD` secret in GitHub

## Lessons Learned

### What Went Wrong
- Documented real credentials in version-controlled files
- Used actual key IDs in example scripts
- Didn't have sensitive files in .gitignore from the start

### Best Practices Going Forward

✅ **DO:**
- Store ALL secrets only in GitHub Secrets (encrypted)
- Use placeholder values in documentation (e.g., `YOUR_KEY_ID`, `YOUR_PASSWORD`)
- Add sensitive file patterns to .gitignore BEFORE committing
- Use automated scripts that don't hardcode credentials
- Regularly audit repository for sensitive data
- Use tools like GitGuardian, git-secrets, or truffleHog

❌ **DON'T:**
- Commit secrets, even in documentation or examples
- Use real credentials in scripts or documentation
- Share private keys in any form
- Leave sensitive files untracked but not ignored

## Verification Checklist

- [ ] Old GPG key revoked and deleted
- [ ] New GPG key generated
- [ ] New public key added to GitHub account
- [ ] GitHub Secrets updated with new key
- [ ] Sonatype password changed (if exposed)
- [ ] Git history cleaned (optional)
- [ ] All sensitive files in .gitignore
- [ ] No secrets in any tracked files
- [ ] Test publishing with new credentials
- [ ] Monitor for any unauthorized use of old credentials

## Additional Security Measures

### Enable GitHub Security Features

1. **Secret Scanning Alerts**
   - Go to: https://github.com/ayoubarka/PixaCompose/settings/security_analysis
   - Enable: Secret scanning
   - Enable: Push protection

2. **Dependabot Alerts**
   - Enable: Dependabot alerts
   - Enable: Dependabot security updates

3. **Code Scanning**
   - Consider enabling CodeQL analysis

### Local Git Hooks

Create `.git/hooks/pre-commit`:

```bash
#!/bin/bash
# Check for potential secrets before committing

if git diff --cached | grep -E "(BEGIN|PRIVATE|SECRET|PASSWORD|passphrase|api[_-]?key)"; then
    echo "⚠️  WARNING: Potential secret detected in commit!"
    echo "Please review your changes carefully."
    read -p "Continue anyway? (y/N): " confirm
    if [ "$confirm" != "y" ]; then
        echo "Commit aborted."
        exit 1
    fi
fi
```

Make it executable:
```bash
chmod +x .git/hooks/pre-commit
```

## Timeline

- **2026-01-10 20:15 UTC**: Key exposed in GitHub push
- **2026-01-10 ~20:30 UTC**: GitGuardian alert received
- **2026-01-10 ~20:40 UTC**: Immediate remediation actions taken
- **Next**: Complete all steps in "Next Steps Required" section

## Contact

If you notice any suspicious activity or unauthorized use:
1. Revoke all credentials immediately
2. Contact GitHub Support: https://support.github.com/
3. Contact Sonatype Support if publishing was affected

## References

- [GitHub Secret Scanning](https://docs.github.com/en/code-security/secret-scanning)
- [GitGuardian Best Practices](https://www.gitguardian.com/secrets-detection)
- [GPG Key Management](https://docs.github.com/en/authentication/managing-commit-signature-verification)
- [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/)

