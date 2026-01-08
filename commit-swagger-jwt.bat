@echo off
echo ========================================
echo Committing Swagger JWT Authentication
echo ========================================
echo.

REM Check current branch
git branch --show-current > temp_branch.txt 2>nul
if %errorlevel% neq 0 (
    echo Git repository not initialized. Initializing...
    git init
    git branch -M main
    set CURRENT_BRANCH=main
) else (
    set /p CURRENT_BRANCH=<temp_branch.txt
    del temp_branch.txt
)

echo Current branch: %CURRENT_BRANCH%
echo.

echo Step 1: Adding all changes...
git add .

echo.
echo Step 2: Committing changes...
git commit -m "feat: Add JWT Bearer token authentication to Swagger UI

- Add JWT Bearer token security scheme to OpenAPI configuration
- Enable Authorize button in Swagger UI for token input
- Configure security requirement for all endpoints
- Users can now set JWT token in Swagger header for API testing
- Token will be automatically included in Authorization header"

if %errorlevel% neq 0 (
    echo.
    echo Error: Failed to commit changes
    echo Please check if there are any issues
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

