@echo off

cd C:\Dateien\Workspace\Eclipse\database
git add -A
git commit -m "update"
for /f %%i in ('git rev-parse @') do set LOCAL=%%i
for /f %%i in ('git rev-parse @{u}') do set REMOTE=%%i
for /f %%i in ('git merge-base @ @{u}') do set BASE=%%i
IF %LOCAL% == %REMOTE% (
	echo databse up-to-date
) ELSE (
	IF %REMOTE% == %BASE% (
			git push --force
		)
	)
	
cd C:\Users\Max\AppData\Roaming\Thunderbird\Profiles\mail\Mail\Local Folders
git add -A
git commit -m "update"
for /f %%i in ('git rev-parse @') do set LOCAL=%%i
for /f %%i in ('git rev-parse @{u}') do set REMOTE=%%i
for /f %%i in ('git merge-base @ @{u}') do set BASE=%%i
IF %LOCAL% == %REMOTE% (
	echo database up-to-date
) ELSE (
	IF %REMOTE% == %BASE% (
			git push --force
		)
	)
pause
exit