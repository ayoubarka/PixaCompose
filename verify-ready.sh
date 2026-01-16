#!/bin/bash

# PixaCompose v1.0.0 - Pre-Publication Verification Script
# This script verifies that everything is ready for Maven Central publication

set -e

echo "🔍 PixaCompose v1.0.0 - Pre-Publication Verification"
echo "=================================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check 1: Git status
echo "1️⃣  Checking Git status..."
if [ -z "$(git status --porcelain)" ]; then
    echo -e "${GREEN}✅ Working directory is clean${NC}"
else
    echo -e "${YELLOW}⚠️  Uncommitted changes detected:${NC}"
    git status --short
    echo ""
fi

# Check 2: Current branch
echo ""
echo "2️⃣  Checking Git branch..."
BRANCH=$(git branch --show-current)
if [ "$BRANCH" == "main" ]; then
    echo -e "${GREEN}✅ On main branch${NC}"
else
    echo -e "${RED}❌ Not on main branch (current: $BRANCH)${NC}"
    exit 1
fi

# Check 3: Remote repository
echo ""
echo "3️⃣  Checking remote repository..."
REMOTE=$(git remote get-url origin)
if [[ "$REMOTE" == *"ayoubarka/PixaCompose"* ]]; then
    echo -e "${GREEN}✅ Remote repository configured: $REMOTE${NC}"
else
    echo -e "${RED}❌ Unexpected remote: $REMOTE${NC}"
    exit 1
fi

# Check 4: Build verification
echo ""
echo "4️⃣  Running build verification..."
echo "   (This may take a few minutes...)"
if ./gradlew :library:assemble --no-daemon --quiet; then
    echo -e "${GREEN}✅ Build successful${NC}"
else
    echo -e "${RED}❌ Build failed${NC}"
    exit 1
fi

# Check 5: Version consistency
echo ""
echo "5️⃣  Checking version consistency..."
VERSION=$(grep "^version =" gradle/libs.versions.toml | cut -d'"' -f2)
if [ "$VERSION" == "1.0.0" ]; then
    echo -e "${GREEN}✅ Version is 1.0.0${NC}"
else
    echo -e "${YELLOW}⚠️  Version in libs.versions.toml: $VERSION${NC}"
fi

# Check 6: Required files
echo ""
echo "6️⃣  Checking required files..."
FILES=(
    "README.md"
    "LICENSE"
    "CHANGELOG.md"
    "library/build.gradle.kts"
    ".github/workflows/publish.yml"
    "IMPLEMENTATION_SUMMARY.md"
    "READY_TO_PUBLISH.md"
)

for file in "${FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✅ $file${NC}"
    else
        echo -e "${RED}❌ Missing: $file${NC}"
    fi
done

# Check 7: Component files
echo ""
echo "7️⃣  Checking component implementations..."
COMPONENTS=(
    "library/src/commonMain/kotlin/com/pixamob/pixacompose/components/inputs/TextField.kt"
    "library/src/commonMain/kotlin/com/pixamob/pixacompose/components/inputs/TextArea.kt"
    "library/src/commonMain/kotlin/com/pixamob/pixacompose/components/inputs/SearchBar.kt"
    "library/src/commonMain/kotlin/com/pixamob/pixacompose/components/inputs/Slider.kt"
    "library/src/commonMain/kotlin/com/pixamob/pixacompose/components/inputs/Switch.kt"
    "library/src/commonMain/kotlin/com/pixamob/pixacompose/components/inputs/Checkbox.kt"
)

for component in "${COMPONENTS[@]}"; do
    if [ -f "$component" ]; then
        echo -e "${GREEN}✅ $(basename $component)${NC}"
    else
        echo -e "${RED}❌ Missing: $(basename $component)${NC}"
    fi
done

# Check 8: GitHub Secrets reminder
echo ""
echo "8️⃣  GitHub Secrets (verify manually on GitHub)..."
echo "   Go to: https://github.com/ayoubarka/PixaCompose/settings/secrets/actions"
echo ""
echo "   Required secrets:"
echo "   - SONATYPE_USERNAME"
echo "   - SONATYPE_PASSWORD"
echo "   - GPG_PRIVATE_KEY"
echo "   - GPG_PASSPHRASE"
echo ""
echo -e "${YELLOW}⚠️  Please verify these secrets are configured on GitHub${NC}"

# Summary
echo ""
echo "=================================================="
echo "📋 VERIFICATION SUMMARY"
echo "=================================================="
echo ""
echo -e "${GREEN}✅ Pre-publication checks completed${NC}"
echo ""
echo "🚀 NEXT STEPS TO PUBLISH:"
echo ""
echo "1. Push all changes to GitHub:"
echo "   git push origin main"
echo ""
echo "2. Create and push release tag:"
echo "   git tag -a v1.0.0 -m \"Release v1.0.0\""
echo "   git push origin v1.0.0"
echo ""
echo "3. Monitor GitHub Actions:"
echo "   https://github.com/ayoubarka/PixaCompose/actions"
echo ""
echo "4. Verify publication (after ~30 minutes):"
echo "   https://central.sonatype.com/artifact/com.pixamob/pixacompose"
echo ""
echo "=================================================="
echo "🎉 Good luck with your release!"
echo "=================================================="

