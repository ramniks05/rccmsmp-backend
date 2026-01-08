@echo off
echo ========================================
echo Committing code changes
echo ========================================
echo.

REM Check current branch
git branch --show-current > temp_branch.txt
set /p CURRENT_BRANCH=<temp_branch.txt
del temp_branch.txt

echo Current branch: %CURRENT_BRANCH%
echo.

REM Check if there are changes to commit
git status --porcelain > temp_status.txt
if %errorlevel% neq 0 (
    echo Error: Git repository not initialized
    echo Please run: git init
    pause
    exit /b 1
)

findstr /R "." temp_status.txt >nul
if %errorlevel% equ 0 (
    echo Changes detected. Proceeding with commit...
    echo.
) else (
    echo No changes to commit.
    pause
    exit /b 0
)
del temp_status.txt

echo Step 1: Adding all changes...
git add .

echo.
echo Step 2: Committing changes...
git commit -m "fix: Fix Swagger access and remove Aadhaar validation

- Fix Swagger UI access paths in security configuration
- Add webjars path for Swagger UI resources
- Update Swagger configuration for better compatibility
- Remove Aadhaar number validation (make it optional)
- Add null safety checks in services
- Improve error handling and validation"

if %errorlevel% neq 0 (
    echo.
    echo Error: Failed to commit changes
    pause
    exit /b 1
)

echo.
echo ========================================
echo Success! Code committed
echo ========================================
echo.
echo Branch: %CURRENT_BRANCH%
echo.
echo To push to remote, run:
echo   git push origin %CURRENT_BRANCH%
echo.
pause

