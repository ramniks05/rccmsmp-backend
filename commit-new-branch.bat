@echo off
echo ========================================
echo Creating new branch and committing code
echo ========================================
echo.

REM Check if git is initialized
if not exist ".git" (
    echo Initializing Git repository...
    git init
    git branch -M main
)

echo.
echo Step 1: Creating new branch 'feature/citizen-registration-login'...
git checkout -b feature/citizen-registration-login
if %errorlevel% neq 0 (
    echo Branch may already exist, switching to it...
    git checkout feature/citizen-registration-login
)

echo.
echo Step 2: Adding all changes...
git add .

echo.
echo Step 3: Committing changes...
git commit -m "feat: Add citizen registration and login functionality

- Add Citizen entity with registration fields
- Implement citizen registration API with password hashing
- Add JWT-based authentication service
- Implement login with mobile/email and password
- Add encryption service for Aadhaar numbers
- Add global exception handling
- Configure CORS for Angular frontend
- Add Swagger/OpenAPI documentation
- Remove Aadhaar validation (optional field)
- Fix null safety warnings and add proper validation"

if %errorlevel% neq 0 (
    echo.
    echo Error: Failed to commit changes
    echo Please check if there are any uncommitted changes
    pause
    exit /b 1
)

echo.
echo ========================================
echo Success! Code committed to new branch
echo ========================================
echo.
echo Branch: feature/citizen-registration-login
echo.
echo To push to remote, run:
echo   git push -u origin feature/citizen-registration-login
echo.
pause

