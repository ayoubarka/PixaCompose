# Quick Reference Guide - PixaCompose Publishing

## 🚨 IMMEDIATE ACTIONS NEEDED

### 1. Configure GitHub Secrets (REQUIRED)

You have 2 options:

#### Option A: Use the automated script (Recommended)
```bash
cd /Users/ayouboubarka/StudioProjects/PixaCompose
./setup-github-secrets.sh
```

This will:
- Export your GPG key automatically
- Prompt you for Sonatype credentials
- Set all 4 GitHub secrets for you

**Requirements:**
- GitHub CLI installed: `brew install gh`
- Authenticated: `gh auth login`

#### Option B: Manual setup

1. Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions

2. Export GPG key:
   ```bash
   gpg --armor --export-secret-keys YOUR_KEY_ID | base64 | pbcopy
   ```
   
   Replace `YOUR_KEY_ID` with your actual GPG key ID.

3. Add these 4 secrets:
   - `SONATYPE_USERNAME` = Your Sonatype email
   - `SONATYPE_PASSWORD` = Your Sonatype password
   - `GPG_PRIVATE_KEY` = Paste from clipboard (base64 string)
   - `GPG_PASSPHRASE` = Your GPG key passphrase

⚠️ **Security Warning**: Never commit actual credentials to Git!

### 2. Verify Current Workflow Status

Check: https://github.com/ayoubarka/PixaCompose/actions

The v1.0.0 tag was just pushed and should trigger the publishing workflow.

**If it's failing:**
1. Configure the secrets (step 1 above)
2. Cancel the running workflow
3. Re-trigger by pushing the tag again:
   ```bash
   git tag -d v1.0.0
   git push origin :refs/tags/v1.0.0
   git tag -a v1.0.0 -m "Release version 1.0.0"
   git push origin v1.0.0
   ```

## 📋 What's Been Fixed

✅ **GitHub Actions Workflow** - Test tasks now properly scoped to `:library:` module  
✅ **GPG Key Import** - Now uses correct passphrase  
✅ **Test Configuration** - Removed invalid test targets  
✅ **Publishing Config** - Properly configured for Sonatype Central Portal  

## 🔍 Current Status

- **Repository**: https://github.com/ayoubarka/PixaCompose
- **Latest Commit**: Pushed to main
- **Tag**: v1.0.0 (recreated and pushed)
- **Workflow**: Should be running (check Actions tab)
- **Secrets**: ⚠️ NEED TO BE CONFIGURED (see step 1 above)

## 📊 Expected Timeline

Once secrets are configured:

1. **0-5 min**: GitHub Actions builds all targets
2. **5-10 min**: Runs tests and signs artifacts
3. **10-15 min**: Publishes to Sonatype Central Portal
4. **15-45 min**: Artifact appears on Maven Central
5. **45-60 min**: Available worldwide via CDN

## ✅ Success Indicators

You'll know it worked when:

1. ✅ GitHub Actions workflow completes successfully (green checkmark)
2. ✅ GitHub Release is created with artifacts
3. ✅ Artifact appears on: https://central.sonatype.com/artifact/com.pixamob/pixacompose
4. ✅ Artifact appears on: https://repo1.maven.org/maven2/com/pixamob/pixacompose/1.0.0/

## 🐛 Common Issues

### Issue: "error decoding base64 input stream"
**Solution**: Make sure GPG_PRIVATE_KEY is the base64-encoded armored key (use the script or command above)

### Issue: "Task 'jvmTest' not found"
**Solution**: ✅ Already fixed - tasks are now scoped to `:library:` module

### Issue: "401 Unauthorized" when publishing
**Solution**: Check SONATYPE_USERNAME and SONATYPE_PASSWORD secrets are correct

### Issue: "No value has been specified for property 'signing.key'"
**Solution**: Check GPG_PRIVATE_KEY and GPG_PASSPHRASE secrets are set

### Issue: Library not appearing on Maven Central after 1 hour
**Solution**: Check Sonatype Central Portal logs and ensure publication completed successfully

## 🔗 Important Links

- **Actions**: https://github.com/ayoubarka/PixaCompose/actions
- **Secrets**: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions
- **Releases**: https://github.com/ayoubarka/PixaCompose/releases
- **Maven Central**: https://central.sonatype.com/artifact/com.pixamob/pixacompose
- **Sonatype Portal**: https://central.sonatype.com/

## 🎯 Next Release

For future releases:

```bash
# 1. Update version in gradle.properties
echo "VERSION_NAME=1.0.1" >> gradle.properties

# 2. Commit changes
git add .
git commit -m "Release v1.0.1"
git push

# 3. Create and push tag
git tag -a v1.0.1 -m "Release version 1.0.1"
git push origin v1.0.1

# 4. Wait for workflow to complete
# 5. Check Maven Central
```

---

**PRIORITY**: Configure the GitHub secrets NOW to allow the current workflow to succeed!

Run: `./setup-github-secrets.sh` or configure manually via the GitHub web interface.

