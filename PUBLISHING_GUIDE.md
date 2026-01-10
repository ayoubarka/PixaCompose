# Publishing Guide for PixaCompose

This guide covers everything you need to know about publishing PixaCompose to Maven Central via Sonatype Central Portal.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Initial Setup](#initial-setup)
3. [Local Publishing](#local-publishing)
4. [Automated CI/CD Publishing](#automated-cicd-publishing)
5. [Troubleshooting](#troubleshooting)
6. [Version Management](#version-management)

---

## Prerequisites

### 1. Sonatype Account

1. Go to [Sonatype Central Portal](https://central.sonatype.com/)
2. Sign up or log in with your account
3. Generate User Token:
   - Go to Account → Generate User Token
   - Save the username and password (these are your publishing credentials)

### 2. Verify Namespace

The namespace `com.pixamob` must be verified with Sonatype:

1. Log into [Sonatype Central Portal](https://central.sonatype.com/)
2. Go to Namespaces
3. Add namespace: `com.pixamob`
4. Verify ownership via:
   - GitHub repository verification (recommended)
   - DNS TXT record
   - Website verification

**Current Status**: ✅ `com.pixamob` namespace is verified

### 3. GPG Key Setup

#### Generate GPG Key

```bash
# Generate a new GPG key pair
gpg --full-generate-key

# Follow prompts:
# - Key type: RSA and RSA
# - Key size: 4096 bits
# - Expiration: 0 (never expires) or set expiration
# - Name: Ayoub Arka
# - Email: ayoub@pixamob.com
# - Passphrase: Choose a strong passphrase
```

#### List Your Keys

```bash
# List all keys
gpg --list-secret-keys --keyid-format=long

# Output example:
# sec   rsa4096/ABCD1234EFGH5678 2025-01-01 [SC]
#       1234567890ABCDEF1234567890ABCDEF12345678
# uid                 [ultimate] Ayoub Arka <ayoub@pixamob.com>
```

The key ID is `ABCD1234EFGH5678` (last 8 characters of the second line).

#### Publish Public Key

```bash
# Publish to multiple keyservers for redundancy
gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
gpg --keyserver keys.openpgp.org --send-keys YOUR_KEY_ID
gpg --keyserver pgp.mit.edu --send-keys YOUR_KEY_ID

# Verify publication (may take a few minutes)
gpg --keyserver keyserver.ubuntu.com --recv-keys YOUR_KEY_ID
```

#### Export Private Key for CI

```bash
# Export private key
gpg --export-secret-keys YOUR_KEY_ID | base64 > private-key-base64.txt

# The content of this file will be used as GPG_PRIVATE_KEY secret in GitHub
```

---

## Initial Setup

### Local Configuration

Create or edit `~/.gradle/gradle.properties`:

```properties
# Sonatype Central Portal Credentials
mavenCentralUsername=YOUR_SONATYPE_USERNAME
mavenCentralPassword=YOUR_SONATYPE_TOKEN_PASSWORD

# GPG Signing Configuration
signing.keyId=LAST_8_CHARS_OF_YOUR_KEY_ID
signing.password=YOUR_GPG_PASSPHRASE
signing.secretKeyRingFile=/Users/YOUR_USERNAME/.gnupg/secring.gpg
```

#### Exporting GPG Secret Keyring

Modern GPG versions don't create `secring.gpg` by default. Export it manually:

```bash
# Export to the old format
gpg --export-secret-keys YOUR_KEY_ID > ~/.gnupg/secring.gpg

# Or specify custom location
gpg --export-secret-keys YOUR_KEY_ID > /path/to/secring.gpg
```

### Project Configuration

The project's `gradle.properties` contains placeholder values:

```properties
# Publishing Configuration
mavenCentralUsername=
mavenCentralPassword=
signing.keyId=
signing.password=
signing.secretKeyRingFile=
```

**Important**: Never commit actual credentials to version control!

---

## Local Publishing

### Step 1: Validate Configuration

```bash
# Verify GPG is configured correctly
gpg --list-secret-keys

# Test GPG signing
echo "test" | gpg --clearsign

# Verify Gradle can read credentials
./gradlew properties | grep maven
./gradlew properties | grep signing
```

### Step 2: Build and Test

```bash
# Clean build
./gradlew clean

# Build all targets
./gradlew :library:build --stacktrace

# Run tests
./gradlew :library:allTests --stacktrace

# Build without tests (if needed)
./gradlew :library:build -x test
```

### Step 3: Publish to Maven Local (Testing)

Before publishing to Maven Central, test locally:

```bash
# Publish to Maven Local (~/.m2/repository)
./gradlew :library:publishToMavenLocal

# Verify files in ~/.m2/repository/com/pixamob/pixacompose/
ls -la ~/.m2/repository/com/pixamob/pixacompose/
```

### Step 4: Publish to Maven Central

```bash
# Publish all publications to Maven Central
./gradlew :library:publishToMavenCentral --no-configuration-cache

# Or publish specific publication
./gradlew :library:publishAndroidReleasePublicationToMavenCentral

# With detailed logging
./gradlew :library:publishToMavenCentral --no-configuration-cache --info
```

### Step 5: Verify Publication

1. **Immediate Check**:
   - Log into [Sonatype Central Portal](https://central.sonatype.com/)
   - Go to Deployments
   - Verify your deployment appears

2. **Maven Central Search** (10-30 minutes delay):
   - Visit: https://central.sonatype.com/artifact/com.pixamob/pixacompose
   - Or: https://search.maven.org/artifact/com.pixamob/pixacompose

3. **Verify Artifacts**:
   - Source JAR (sources)
   - Javadoc JAR (javadoc)
   - Module metadata
   - GPG signatures (.asc files)
   - POM file with complete metadata

---

## Automated CI/CD Publishing

### GitHub Actions Setup

The repository includes `.github/workflows/publish.yml` for automated publishing.

### Configure GitHub Secrets

1. Go to your repository on GitHub
2. Navigate to: **Settings → Secrets and variables → Actions**
3. Add the following secrets:

| Secret Name | Value | Description |
|------------|-------|-------------|
| `SONATYPE_USERNAME` | Your Sonatype username | From User Token |
| `SONATYPE_PASSWORD` | Your Sonatype password | From User Token |
| `GPG_PRIVATE_KEY` | Base64 encoded private key | Content of `private-key-base64.txt` |
| `GPG_PASSPHRASE` | Your GPG passphrase | Passphrase used when creating key |

### Triggering Automated Publishing

#### Via Git Tags

```bash
# 1. Update version in gradle.properties or libs.versions.toml
# Edit: gradle/libs.versions.toml
# appVersionName = "1.0.0"

# 2. Commit changes
git add .
git commit -m "Release version 1.0.0"

# 3. Create annotated tag
git tag -a v1.0.0 -m "Release version 1.0.0"

# 4. Push commits and tag
git push origin main
git push origin v1.0.0
```

#### Via Manual Workflow Dispatch

1. Go to **Actions** tab on GitHub
2. Select **Publish to Maven Central** workflow
3. Click **Run workflow**
4. Choose branch and click **Run workflow**

### Workflow Process

The GitHub Action will:

1. ✅ Checkout repository
2. ✅ Set up JDK 17
3. ✅ Validate Gradle wrapper
4. ✅ Import GPG key
5. ✅ Build all targets (Android, iOS)
6. ✅ Run tests
7. ✅ Sign artifacts with GPG
8. ✅ Publish to Maven Central
9. ✅ Create GitHub Release with artifacts
10. ✅ Cleanup GPG keys

### Monitoring Publication

1. **GitHub Actions**:
   - Go to Actions tab
   - Click on the running workflow
   - Monitor each step in real-time

2. **Sonatype Portal**:
   - Check Deployments section
   - Verify deployment status

3. **Maven Central**:
   - Wait 10-30 minutes for sync
   - Check https://central.sonatype.com/artifact/com.pixamob/pixacompose

---

## Troubleshooting

### Common Issues

#### 1. GPG Signing Failures

**Error**: `gpg: signing failed: No secret key`

**Solution**:
```bash
# Verify key exists
gpg --list-secret-keys

# Check keyId matches
./gradlew properties | grep signing.keyId

# Ensure secring.gpg exists
ls -la ~/.gnupg/secring.gpg

# Re-export if needed
gpg --export-secret-keys YOUR_KEY_ID > ~/.gnupg/secring.gpg
```

#### 2. Authentication Failures

**Error**: `401 Unauthorized` or `403 Forbidden`

**Solution**:
```bash
# Verify credentials in ~/.gradle/gradle.properties
cat ~/.gradle/gradle.properties | grep mavenCentral

# Test credentials manually
curl -u "USERNAME:PASSWORD" https://central.sonatype.com/api/v1/publisher/status

# Regenerate User Token if expired
# Go to Sonatype Central Portal → Account → Generate User Token
```

#### 3. Namespace Verification Issues

**Error**: `Namespace com.pixamob is not verified`

**Solution**:
1. Log into Sonatype Central Portal
2. Go to Namespaces
3. Verify you have `com.pixamob` listed
4. Complete verification process if pending
5. Wait for verification approval (can take 24-48 hours)

#### 4. Missing Artifacts

**Error**: `Publication rejected: Missing sources or javadoc`

**Solution**:
The Vanniktech plugin automatically generates these. Verify in build output:
```bash
./gradlew :library:publishToMavenLocal --info | grep -i "jar"
```

Should see:
- `pixacompose-1.0.0.jar`
- `pixacompose-1.0.0-sources.jar`
- `pixacompose-1.0.0-javadoc.jar`

#### 5. Configuration Cache Issues

**Error**: Configuration cache problems

**Solution**:
```bash
# Disable configuration cache for publishing
./gradlew :library:publishToMavenCentral --no-configuration-cache

# Or temporarily disable in gradle.properties
org.gradle.configuration-cache=false
```

#### 6. POM Validation Errors

**Error**: `Invalid POM: missing required metadata`

**Solution**:
Verify `build.gradle.kts` contains all required POM fields:
- name
- description
- url
- licenses
- developers
- scm

#### 7. Multiplatform Publication Issues

**Error**: Only Android artifacts published, iOS missing

**Solution**:
```bash
# Build all targets explicitly
./gradlew :library:assemble --stacktrace

# Check available publications
./gradlew :library:tasks --group publishing

# Publish all publications
./gradlew :library:publishAllPublicationsToMavenCentral
```

### Debugging Tips

#### Verbose Logging

```bash
# Info level
./gradlew :library:publishToMavenCentral --info

# Debug level
./gradlew :library:publishToMavenCentral --debug

# Stack traces
./gradlew :library:publishToMavenCentral --stacktrace
```

#### Inspect Generated Artifacts

```bash
# List all build outputs
find library/build -name "*.jar" -o -name "*.aar" -o -name "*.pom"

# Check POM contents
cat library/build/publications/release/pom-default.xml

# Verify signatures
find library/build -name "*.asc"
```

#### Test GPG Independently

```bash
# Create test file
echo "test" > test.txt

# Sign it
gpg --detach-sign --armor test.txt

# Verify signature
gpg --verify test.txt.asc test.txt

# Cleanup
rm test.txt test.txt.asc
```

---

## Version Management

### Semantic Versioning

PixaCompose follows [Semantic Versioning 2.0.0](https://semver.org/):

- **MAJOR** (1.0.0): Breaking API changes
- **MINOR** (0.1.0): New features, backward compatible
- **PATCH** (0.0.1): Bug fixes, backward compatible

### Updating Version

Edit `gradle/libs.versions.toml`:

```toml
[versions]
appVersionName = "1.0.0"  # Update this
```

### Pre-release Versions

For alpha, beta, or RC releases:

```toml
appVersionName = "1.0.0-alpha01"
appVersionName = "1.0.0-beta01"
appVersionName = "1.0.0-rc01"
```

### Snapshot Versions

For development versions:

```toml
appVersionName = "1.0.0-SNAPSHOT"
```

**Note**: SNAPSHOT versions are not supported by Sonatype Central Portal (only by OSSRH).

---

## Release Checklist

Use this checklist for each release:

### Pre-Release

- [ ] All tests passing locally
- [ ] All CI checks passing
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Version number updated
- [ ] README.md examples use correct version
- [ ] No breaking changes (or documented if MAJOR bump)
- [ ] All dependencies up to date

### Release Process

- [ ] Commit all changes
- [ ] Create git tag
- [ ] Push commits and tag
- [ ] Monitor GitHub Actions
- [ ] Verify Sonatype deployment
- [ ] Wait for Maven Central sync (10-30 min)
- [ ] Test dependency from Maven Central

### Post-Release

- [ ] Create GitHub Release with release notes
- [ ] Update documentation site (if applicable)
- [ ] Announce on social media/blog
- [ ] Update samples/examples
- [ ] Bump version to next development version
- [ ] Close milestone on GitHub (if used)

---

## Additional Resources

### Documentation

- [Sonatype Central Portal Guide](https://central.sonatype.org/publish/publish-guide/)
- [Vanniktech Maven Publish Plugin](https://github.com/vanniktech/gradle-maven-publish-plugin)
- [GPG Documentation](https://www.gnupg.org/documentation/)
- [Gradle Publishing Guide](https://docs.gradle.org/current/userguide/publishing_maven.html)

### Support

- **Issues**: [GitHub Issues](https://github.com/ayoubarka/PixaCompose/issues)
- **Email**: ayoub@pixamob.com
- **Sonatype Support**: [Sonatype Help](https://central.sonatype.org/support/)

---

## Security Notes

### Credential Management

⚠️ **Never commit credentials to version control!**

- Use `~/.gradle/gradle.properties` for local credentials
- Use GitHub Secrets for CI credentials
- Add `*.properties`, `*.gpg`, `*.key` to `.gitignore`
- Rotate credentials regularly
- Use different credentials for different environments

### GPG Key Management

- Backup your GPG key securely (offline storage)
- Use a strong passphrase
- Set expiration date (recommended: 2-5 years)
- Publish public key to multiple keyservers
- Revoke key if compromised

### GitHub Secrets

- Regularly rotate secrets
- Use minimal permissions
- Never log secrets in CI output
- Review access logs periodically

---

**Last Updated**: January 9, 2025  
**Author**: Ayoub Arka  
**Repository**: [PixaCompose](https://github.com/ayoubarka/PixaCompose)

