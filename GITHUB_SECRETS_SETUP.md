# GitHub Secrets Configuration Guide

## Current Status
- ✅ Code pushed to GitHub
- ✅ Tag v1.0.0 created and pushed
- ⚠️ GitHub Secrets NOT configured yet
- ⚠️ GitHub Actions workflow will FAIL without secrets

## Why Maven Central Shows 404

The 404 error at https://central.sonatype.com/artifact/com.pixamob/pixacompose is **EXPECTED** because:

1. ❌ GitHub Secrets are not configured yet
2. ❌ GitHub Actions workflow cannot authenticate
3. ❌ No artifacts have been published yet
4. ✅ This is normal - publication happens AFTER secrets are set

---

## Step-by-Step: Configure GitHub Secrets

### 1. Navigate to Repository Settings

Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions

Or follow these steps:
1. Open: https://github.com/ayoubarka/PixaCompose
2. Click **Settings** (top right)
3. In left sidebar: **Secrets and variables** → **Actions**
4. Click **New repository secret**

---

### 2. Add Required Secrets (4 Total)

#### Secret 1: SONATYPE_USERNAME

**Steps to get this value:**

1. Go to: https://central.sonatype.com/
2. Click **Log in** (or Sign up if you don't have an account)
3. After login, click your profile icon → **Account**
4. Click **Generate User Token**
5. Copy the **Username** value

**Add to GitHub:**
- Name: `SONATYPE_USERNAME`
- Value: [Paste the username from Sonatype]
- Click **Add secret**

---

#### Secret 2: SONATYPE_PASSWORD

**Steps to get this value:**

From the same User Token generation page above:
- Copy the **Password** value

**Add to GitHub:**
- Name: `SONATYPE_PASSWORD`
- Value: [Paste the password from Sonatype]
- Click **Add secret**

---

#### Secret 3: GPG_PRIVATE_KEY

**Steps to generate and add:**

**If you already have a GPG key:**
```bash
# List your keys to find the key ID
gpg --list-secret-keys --keyid-format=long

# You'll see output like:
# sec   rsa4096/ABCD1234EFGH5678 2025-01-10 [SC]

# Export and encode (replace YOUR_KEY_ID with actual ID)
gpg --export-secret-keys YOUR_KEY_ID | base64 > gpg-key-base64.txt

# View the encoded key
cat gpg-key-base64.txt
```

**If you DON'T have a GPG key yet:**
```bash
# Generate new GPG key
gpg --full-generate-key

# Choose:
# - Key type: RSA and RSA
# - Key size: 4096 bits
# - Expiration: 0 (never) or set as desired
# - Name: Ayoub Arka
# - Email: ayoub@pixamob.com
# - Passphrase: [Choose a strong passphrase - REMEMBER THIS!]

# After generation, list keys
gpg --list-secret-keys --keyid-format=long

# Find your key ID (8 character hex after rsa4096/)
# Example: ABCD1234EFGH5678

# Publish public key to keyservers
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID

# Export private key as base64
gpg --export-secret-keys YOUR_KEY_ID | base64 > gpg-key-base64.txt

# View the content
cat gpg-key-base64.txt
```

**Add to GitHub:**
- Name: `GPG_PRIVATE_KEY`
- Value: [Paste the ENTIRE content from gpg-key-base64.txt]
- Click **Add secret**

**Important**: The value should be a long base64 string starting with something like `lQdGBGb...`

---

#### Secret 4: GPG_PASSPHRASE

This is the passphrase you used when creating your GPG key.

**Add to GitHub:**
- Name: `GPG_PASSPHRASE`
- Value: [Your GPG key passphrase]
- Click **Add secret**

---

## Step 3: Verify Secrets Are Set

After adding all 4 secrets, you should see:

```
SONATYPE_USERNAME         Updated X seconds ago
SONATYPE_PASSWORD         Updated X seconds ago
GPG_PRIVATE_KEY           Updated X seconds ago
GPG_PASSPHRASE            Updated X seconds ago
```

---

## Step 4: Trigger GitHub Actions Workflow

Since you already pushed the v1.0.0 tag, the workflow should have triggered automatically.

**Check workflow status:**
1. Go to: https://github.com/ayoubarka/PixaCompose/actions
2. You should see a workflow run for "v1.0.0"
3. Click on it to see the progress

**If the workflow already ran and FAILED (before secrets were set):**

You need to re-run it:

**Option A: Re-run from GitHub UI**
1. Go to: https://github.com/ayoubarka/PixaCompose/actions
2. Click on the failed workflow run
3. Click **Re-run all jobs** (top right)

**Option B: Delete and recreate the tag**
```bash
# Delete local tag
git tag -d v1.0.0

# Delete remote tag
git push origin :refs/tags/v1.0.0

# Recreate and push
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

**Option C: Trigger manually**
1. Go to: https://github.com/ayoubarka/PixaCompose/actions
2. Click on **Publish to Maven Central** workflow
3. Click **Run workflow** button
4. Select branch: main
5. Click **Run workflow**

---

## Step 5: Monitor Publication

### Watch GitHub Actions (Real-time)

Visit: https://github.com/ayoubarka/PixaCompose/actions

You should see the workflow progressing through these steps:
- ✅ Checkout Repository
- ✅ Set up JDK 17
- ✅ Validate Gradle Wrapper
- ✅ Grant Execute Permission for Gradlew
- ✅ Import GPG Key
- ✅ Build All Targets
- ✅ Run Tests
- ✅ Publish to Maven Central ← This is the critical step
- ✅ Create GitHub Release
- ✅ Cleanup

**If any step fails**, click on it to see detailed logs.

### Check Sonatype Portal (Immediate)

After "Publish to Maven Central" step succeeds:

1. Go to: https://central.sonatype.com/
2. Click **Log in**
3. Go to **Deployments** (left sidebar)
4. You should see your deployment: `com.pixamob:pixacompose:1.0.0`
5. Status will show as "PUBLISHED" or "VALIDATED"

### Check Maven Central (10-30 minutes later)

After Sonatype deployment succeeds, wait 10-30 minutes for sync:

- https://central.sonatype.com/artifact/com.pixamob/pixacompose
- https://search.maven.org/artifact/com.pixamob/pixacompose

The 404 will disappear once sync completes.

---

## Troubleshooting

### Issue: Workflow fails at "Import GPG Key"

**Error**: `gpg: invalid radix64 character`

**Solution**: 
- Verify GPG_PRIVATE_KEY is properly base64 encoded
- Ensure no line breaks or spaces were added when copying
- Regenerate: `gpg --export-secret-keys YOUR_KEY_ID | base64 | tr -d '\n' > gpg-key.txt`

### Issue: Workflow fails at "Publish to Maven Central"

**Error**: `401 Unauthorized`

**Solution**:
- Verify SONATYPE_USERNAME and SONATYPE_PASSWORD are correct
- Regenerate User Token at https://central.sonatype.com/
- Update GitHub Secrets with new values

### Issue: Namespace not verified

**Error**: `namespace com.pixamob is not verified`

**Solution**:
1. Go to: https://central.sonatype.com/
2. Click **Namespaces**
3. Verify `com.pixamob` is listed and status is "Verified"
4. If not verified, follow the verification process (GitHub repo verification is easiest)

### Issue: GPG signing fails

**Error**: `gpg: signing failed: No secret key`

**Solution**:
- Verify GPG_PRIVATE_KEY secret is set correctly
- Verify GPG_PASSPHRASE is correct
- Ensure GPG key was published to keyservers

---

## Quick Reference: Command Summary

```bash
# Generate GPG key (if needed)
gpg --full-generate-key

# List keys
gpg --list-secret-keys --keyid-format=long

# Publish public key
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID

# Export private key for GitHub
gpg --export-secret-keys YOUR_KEY_ID | base64 > gpg-key-base64.txt

# View encoded key
cat gpg-key-base64.txt

# Local test before pushing
./gradlew :library:publishToMavenLocal
```

---

## Expected Timeline

1. **Configure Secrets**: 5-10 minutes
2. **Trigger Workflow**: Instant (or re-run)
3. **GitHub Actions Run**: 10-15 minutes
4. **Sonatype Processing**: Immediate after workflow
5. **Maven Central Sync**: 10-30 minutes

**Total**: 30-60 minutes from secrets configuration to Maven Central visibility

---

## What Happens After Successful Publication

1. ✅ Artifacts appear on Maven Central
2. ✅ GitHub Release is created with artifacts
3. ✅ Library is searchable: `com.pixamob:pixacompose:1.0.0`
4. ✅ Users can add to their projects:
   ```kotlin
   dependencies {
       implementation("com.pixamob:pixacompose:1.0.0")
   }
   ```

---

## Next Steps After Publication

1. ✅ Update README badge to show Maven Central version
2. ✅ Announce release on social media
3. ✅ Create documentation site (optional)
4. ✅ Add more examples to repository
5. ✅ Plan next version features

---

## Need Help?

**Check workflow logs:**
https://github.com/ayoubarka/PixaCompose/actions

**Sonatype support:**
https://central.sonatype.org/support/

**GitHub Issues:**
https://github.com/ayoubarka/PixaCompose/issues

---

**Current Status**: ⚠️ Waiting for GitHub Secrets configuration

**Next Action**: Configure the 4 secrets at:
https://github.com/ayoubarka/PixaCompose/settings/secrets/actions

Once secrets are configured, the workflow will automatically publish to Maven Central! 🚀

