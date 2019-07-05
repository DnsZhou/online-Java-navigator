REM Use following command before you turn on the case sensitive
REM Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Windows-Subsystem-Linux

REM change the path to your index folder of project
fsutil file SetCaseSensitiveInfo E:\RedHatWorkspaces\online-Java-navigator\tmp\output\Index enable
PAUSE