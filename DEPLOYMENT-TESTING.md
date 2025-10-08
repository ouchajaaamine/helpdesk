# Quick deployment testing commands

# Method 1: Use the deploy-only pipeline
echo "To test deployment only:"
echo "1. Backup current .gitlab-ci.yml:"
echo "   mv .gitlab-ci.yml .gitlab-ci-full.yml"
echo ""
echo "2. Use deploy-only pipeline:"
echo "   mv .gitlab-ci-deploy-only.yml .gitlab-ci.yml"
echo ""
echo "3. Commit and push:"
echo "   git add .gitlab-ci.yml"
echo "   git commit -m 'Test: deploy-only pipeline'"
echo "   git push origin main"
echo ""
echo "4. After testing, restore full pipeline:"
echo "   mv .gitlab-ci-full.yml .gitlab-ci.yml"
echo "   git add .gitlab-ci.yml"
echo "   git commit -m 'Restore: full pipeline'"
echo "   git push origin main"

# Method 2: Skip stages temporarily in main pipeline
echo ""
echo "Method 2: Add 'except: [main]' to stages you want to skip"
echo "This will make those stages not run on main branch"

# Method 3: Use GitLab UI to trigger specific jobs
echo ""
echo "Method 3: In GitLab UI, you can:"
echo "- Go to CI/CD > Pipelines"
echo "- Click 'Run Pipeline'"
echo "- Select specific job to run"