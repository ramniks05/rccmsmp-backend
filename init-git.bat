@echo off
echo Initializing Git repository...
cd /d "%~dp0"

echo.
echo Step 1: Initializing Git repository...
git init
if %errorlevel% neq 0 (
    echo Error: Failed to initialize Git repository
    pause
    exit /b 1
)

echo.
echo Step 2: Adding all files...
git add .
if %errorlevel% neq 0 (
    echo Error: Failed to add files
    pause
    exit /b 1
)

echo.
echo Step 3: Creating initial commit...
git commit -m "Initial commit: Spring Boot project setup"
if %errorlevel% neq 0 (
    echo Error: Failed to create commit
    pause
    exit /b 1
)

echo.
echo Step 4: Setting main branch...
git branch -M main
if %errorlevel% neq 0 (
    echo Warning: Failed to rename branch (may already be main)
)

echo.
echo Step 5: Adding remote repository...
git remote add origin https://github.com/ramniks05/rccmsmp-backend.git
if %errorlevel% neq 0 (
    echo Warning: Remote may already exist, trying to set URL...
    git remote set-url origin https://github.com/ramniks05/rccmsmp-backend.git
)

echo.
echo Step 6: Pushing to GitHub...
echo Note: You may be prompted for GitHub credentials
git push -u origin main
if %errorlevel% neq 0 (
    echo.
    echo Error: Failed to push to GitHub
    echo Please check your GitHub credentials and try again
    echo You can push manually using: git push -u origin main
    pause
    exit /b 1
)

echo.
echo ========================================
echo Success! Repository initialized and pushed to GitHub
echo ========================================
pause

