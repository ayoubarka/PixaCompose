#!/bin/bash

# PixaCompose - Git Push Script
# This script prepares and pushes the repository to GitHub

set -e  # Exit on error

echo "======================================"
echo "PixaCompose GitHub Push Script"
echo "======================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Repository configuration
REPO_URL="https://github.com/ayoubarka/PixaCompose.git"
BRANCH="main"

# Step 1: Security check
echo -e "${YELLOW}Step 1: Running security check...${NC}"
echo "Checking for potential credential leaks..."

# Check for common credential patterns
if grep -r "password.*=.*[a-zA-Z0-9]" --exclude-dir=.git --exclude-dir=build --exclude="*.md" . 2>/dev/null | grep -v "gradle.properties"; then
    echo -e "${RED}ERROR: Found potential credentials in files!${NC}"
    echo "Please remove credentials before pushing."
    exit 1
fi

echo -e "${GREEN}✓ No credentials found${NC}"
echo ""

# Step 2: Git status
echo -e "${YELLOW}Step 2: Checking git status...${NC}"
git status --short
echo ""

# Step 3: Add all files
echo -e "${YELLOW}Step 3: Staging files...${NC}"
git add .
echo -e "${GREEN}✓ Files staged${NC}"
echo ""

# Step 4: Show what will be committed
echo -e "${YELLOW}Step 4: Files to be committed:${NC}"
git diff --cached --name-only
echo ""

# Step 5: Commit
echo -e "${YELLOW}Step 5: Creating commit...${NC}"
read -p "Enter commit message (or press Enter for default): " COMMIT_MSG

if [ -z "$COMMIT_MSG" ]; then
    COMMIT_MSG="Complete setup: PixaCompose v1.0.0

- Maven Central publishing configured (Sonatype Central Portal)
- TextField, TextArea, SearchBar components implemented
- Enhanced ComponentSize system for all component types
- Complete Material 3 theming system
- GitHub Actions CI/CD workflow
- Comprehensive documentation
- Ready for production release"
fi

git commit -m "$COMMIT_MSG"
echo -e "${GREEN}✓ Commit created${NC}"
echo ""

# Step 6: Add remote if not exists
echo -e "${YELLOW}Step 6: Checking remote...${NC}"
if ! git remote | grep -q origin; then
    echo "Adding remote origin..."
    git remote add origin "$REPO_URL"
    echo -e "${GREEN}✓ Remote added${NC}"
else
    echo -e "${GREEN}✓ Remote already exists${NC}"
fi
echo ""

# Step 7: Push
echo -e "${YELLOW}Step 7: Pushing to GitHub...${NC}"
echo "Target: $REPO_URL"
echo "Branch: $BRANCH"
echo ""
read -p "Ready to push? (y/n): " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]; then
    git push -u origin "$BRANCH"
    echo -e "${GREEN}✓ Successfully pushed to GitHub!${NC}"
else
    echo -e "${YELLOW}Push cancelled by user${NC}"
    exit 0
fi

echo ""
echo "======================================"
echo -e "${GREEN}SUCCESS!${NC}"
echo "======================================"
echo ""
echo "Next steps:"
echo "1. Visit: https://github.com/ayoubarka/PixaCompose"
echo "2. Configure GitHub Secrets (Settings → Secrets → Actions)"
echo "   - SONATYPE_USERNAME"
echo "   - SONATYPE_PASSWORD"
echo "   - GPG_PRIVATE_KEY"
echo "   - GPG_PASSPHRASE"
echo "3. Create a release tag:"
echo "   git tag -a v1.0.0 -m 'Release version 1.0.0'"
echo "   git push origin v1.0.0"
echo ""
echo "GitHub Actions will automatically publish to Maven Central!"
echo ""

