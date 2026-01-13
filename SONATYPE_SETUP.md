# ✅ Maven Central Publication - Complete Setup Guide

## Your Sonatype Credentials

Your credentials format is **CORRECT**:

| Field | Value | Status |
|-------|-------|--------|
| Username | `cw1V13` | ✅ Correct (NOT email) |
| Password/Token | `9X56TmXAgfcRJ4XWwgipNYWJF1Aichx6I` | ✅ Correct |
| Group ID | `com.pixamob` | ✅ Verified namespace |
| Artifact ID | `pixacompose` | ✅ Correct |

**IMPORTANT**: The username is `cw1V13`, NOT your email address.

---

## GitHub Secrets Configuration

Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions

Add these 4 secrets:

### 1. SONATYPE_USERNAME
```
cw1V13
```

### 2. SONATYPE_PASSWORD
```
9X56TmXAgfcRJ4XWwgipNYWJF1Aichx6I
```

### 3. GPG_PRIVATE_KEY
This should be your **base64-encoded** GPG private key. Generate it with:

```bash
# List your GPG keys to find KEY_ID
gpg --list-secret-keys --keyid-format LONG

# Export and encode (replace KEY_ID with your actual key ID)
gpg --armor --export-secret-keys KEY_ID | base64 -w 0 | pbcopy

# The base64-encoded key is now in clipboard
# Paste it as the GPG_PRIVATE_KEY secret
```

### 4. GPG_PASSPHRASE
The passphrase you set when creating your GPG key.

---

## Why v1.0.0 Isn't on Maven Central Yet

The tag `v1.0.0` exists, but here's what likely happened:

### ❌ Possible Issue #1: Credentials Not Set
If GitHub Secrets weren't configured when the tag was pushed, the workflow couldn't publish.

**Solution**: Set the secrets now, then retry with a new tag.

### ❌ Possible Issue #2: Workflow Failed Silently
The workflow may have run but failed due to missing credentials.

**Solution**: Check GitHub Actions logs at:
https://github.com/ayoubarka/PixaCompose/actions

### ❌ Possible Issue #3: GPG Key Format Issue
If the GPG_PRIVATE_KEY secret was incorrect format, signing would fail.

**Solution**: Re-export and encode the key correctly (see above).

---

## How to Publish v1.0.0 NOW

Since v1.0.0 tag already exists but wasn't published, follow these steps:

### Step 1: Set GitHub Secrets
1. Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions
2. Add the 4 secrets above (SONATYPE_USERNAME, SONATYPE_PASSWORD, GPG_PRIVATE_KEY, GPG_PASSPHRASE)
3. Click "Save"

### Step 2: Delete the Old Tag

```bash
# Delete locally
git tag -d v1.0.0

# Delete on GitHub
git push origin :refs/tags/v1.0.0
```

### Step 3: Create a New Tag

```bash
# Create new tag (GitHub Actions will trigger when pushed)
git tag -a v1.0.0 -m "Release v1.0.0 - PixaCompose Compose Multiplatform UI Library"

# Push tag to GitHub (this triggers the publish workflow)
git push origin v1.0.0
```

### Step 4: Monitor the Workflow

1. Go to: https://github.com/ayoubarka/PixaCompose/actions
2. Look for "Publish to Maven Central" workflow
3. Watch it complete:
   - ✅ Checkout
   - ✅ Setup JDK
   - ✅ Import GPG Key
   - ✅ Build all targets
   - ✅ Run tests
   - ✅ Publish to Maven Central
   - ✅ Create GitHub release

### Step 5: Verify Publication

After workflow completes (5-10 minutes), verify at:

**Option A - Sonatype Central Portal** (instant):
https://central.sonatype.com/publishing

**Option B - Search Maven Central** (after ~60 minutes):
https://central.sonatype.com/artifact/com.pixamob/pixacompose

**Option C - Direct Maven URL** (after ~60 minutes):
https://repo1.maven.org/maven2/com/pixamob/pixacompose/

**Option D - GitHub Releases**:
https://github.com/ayoubarka/PixaCompose/releases

---

## Troubleshooting - What to Check

### If Workflow Fails:

1. **Check GitHub Actions Log**:
   - Go to: https://github.com/ayoubarka/PixaCompose/actions
   - Click the failed workflow
   - Scroll through logs for error messages

2. **Common Errors**:

   ```
   ERROR: 401 Unauthorized
   → Your SONATYPE_USERNAME or SONATYPE_PASSWORD is wrong
   
   ERROR: gpg: signing failed: No secret key  
   → Your GPG_PRIVATE_KEY is not properly base64-encoded
   
   ERROR: gpg: decryption failed: No secret key
   → Your GPG_PASSPHRASE is incorrect
   
   ERROR: Task linuxX64Test not found
   → This is fixed in gradle.yml (only tests iOS targets)
   ```

3. **Build Fails**:
   - Test locally: `./gradlew :library:build --no-daemon`
   - If it fails locally, fix it before pushing tags

### If Library Still Doesn't Appear After 1 Hour:

1. Check Sonatype Central Portal: https://central.sonatype.com/publishing
2. Look for your library in "Publishing" tab
3. Check for validation messages or errors
4. If blocked, fix issues and try again

---

## Documentation Files

Your repository now has clean documentation:

| File | Purpose |
|------|---------|
| `README.md` | Library overview, features, quick start |
| `COMPONENTS.md` | Detailed component documentation with all parameters |
| `HOW_TO_PUBLISH.md` | Publishing guide for contributors |
| `CONTRIBUTING.md` | How to contribute to the project |
| `CHANGELOG.md` | Version history |
| `verify-ready.sh` | Pre-publication verification script |

---

## Next Actions

✅ **You've done**:
- ✅ Implemented Slider and Switch components
- ✅ Fixed TextField compilation errors
- ✅ Fixed CI workflow
- ✅ Created comprehensive documentation
- ✅ Cleaned up unnecessary files
- ✅ Have valid Sonatype credentials

🔧 **You need to do**:
1. **Configure GitHub Secrets** (4 secrets)
2. **Delete old v1.0.0 tag and recreate it**
3. **Push new tag to trigger workflow**
4. **Monitor GitHub Actions**
5. **Verify publication on Maven Central**

---

## Direct Commands to Execute

```bash
# 1. Delete old tag
git tag -d v1.0.0
git push origin :refs/tags/v1.0.0

# 2. Create new tag
git tag -a v1.0.0 -m "Release v1.0.0 - PixaCompose Compose Multiplatform UI Library"

# 3. Push tag (triggers workflow)
git push origin v1.0.0

# 4. Check if tag is on remote
git ls-remote --tags origin | grep v1.0.0

# 5. Monitor workflow at:
# https://github.com/ayoubarka/PixaCompose/actions
```

---

## Important Notes

⚠️ **DO NOT**:
- Commit GPG keys to the repository
- Commit Sonatype credentials to the repository
- Use your email as username (use `cw1V13`)
- Publish without GitHub Secrets configured

✅ **DO**:
- Use GitHub Secrets for all credentials
- Keep HOW_TO_PUBLISH.md in the repository (it's documentation!)
- Tag releases with semantic versioning (v1.0.0, v1.0.1, v1.1.0, etc.)
- Monitor GitHub Actions for each release
- Keep CHANGELOG.md updated

---

## Support Resources

- **Sonatype Central Portal**: https://central.sonatype.com/
- **GitHub Actions Logs**: https://github.com/ayoubarka/PixaCompose/actions
- **Published Library Search**: https://central.sonatype.com/search?q=com.pixamob.pixacompose
- **Maven Central Mirror**: https://repo1.maven.org/maven2/com/pixamob/pixacompose/

---

**Ready to publish?** 🚀

1. Set GitHub Secrets now
2. Execute the commands above
3. Watch it publish automatically!

Questions? Check the `HOW_TO_PUBLISH.md` file for detailed troubleshooting.

