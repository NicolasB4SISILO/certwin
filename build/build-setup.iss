[Setup]
AppId=Certwin
AppName=certwin
AppVersion=1.0.0
AppPublisher=Nicolas Basilio
DefaultDirName={autopf}\Certwin
DefaultGroupName=certwin
OutputDir=..\builded\setup
OutputBaseFilename=certwin-setup
PrivilegesRequired=admin
ArchitecturesInstallIn64BitMode=x64compatible
ChangesEnvironment=yes
UninstallDisplayIcon={app}\certwin.exe

[Files]
Source: "..\builded\app-image\certwin\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Dirs]
Name: "{app}\app"; Permissions: users-modify

[Registry]
Root: HKLM; Subkey: "SYSTEM\CurrentControlSet\Control\Session Manager\Environment"; ValueType: string; ValueName: "CERTWIN_HOME"; ValueData: "{app}"; Flags: uninsdeletevalue

[UninstallDelete]
Type: filesandordirs; Name: "{app}"

[Code]
const
  EnvironmentKey =
    'SYSTEM\CurrentControlSet\Control\Session Manager\Environment';

function NormalizePath(Value: string): string;
begin
  Value := Trim(Value);
  while (Length(Value) > 3) and
        (Value[Length(Value)] = '\') do
    Delete(Value, Length(Value), 1);
  Result := Value;
end;

function SplitPath(const Value: string): TArrayOfString;
var
  Rest: string;
  P: Integer;
begin
  Rest := Value;
  Result := [];

  while Rest <> '' do
  begin
    P := Pos(';', Rest);

    if P = 0 then
    begin
      SetArrayLength(Result, GetArrayLength(Result) + 1);
      Result[GetArrayLength(Result) - 1] := Rest;
      Rest := '';
    end
    else
    begin
      SetArrayLength(Result, GetArrayLength(Result) + 1);
      Result[GetArrayLength(Result) - 1] := Copy(Rest, 1, P - 1);
      Delete(Rest, 1, P);
    end;
  end;
end;

function PathContainsEntry(CurrentPath, Entry: string): Boolean;
var
  Parts: TArrayOfString;
  I: Integer;
begin
  Result := False;
  Parts := SplitPath(CurrentPath);

  for I := 0 to GetArrayLength(Parts) - 1 do
  begin
    if CompareText(
      NormalizePath(Parts[I]),
      NormalizePath(Entry)
    ) = 0 then
    begin
      Result := True;
      Exit;
    end;
  end;
end;

procedure ModifySystemPath(RemoveEntry: Boolean);
var
  CurrentPath, InstallPath, NewPath: string;
  Parts: TArrayOfString;
  I: Integer;
begin
  InstallPath := NormalizePath(ExpandConstant('{app}'));

  if not RegQueryStringValue(
    HKEY_LOCAL_MACHINE,
    EnvironmentKey,
    'Path',
    CurrentPath
  ) then
    CurrentPath := '';

  if RemoveEntry then
  begin
    Parts := SplitPath(CurrentPath);
    NewPath := '';

    for I := 0 to GetArrayLength(Parts) - 1 do
    begin
      if CompareText(
        NormalizePath(Parts[I]),
        InstallPath
      ) <> 0 then
      begin
        if NewPath <> '' then
          NewPath := NewPath + ';';

        NewPath := NewPath + Parts[I];
      end;
    end;

    CurrentPath := NewPath;
  end
  else
  begin
    if PathContainsEntry(CurrentPath, InstallPath) then
      Exit;

    if CurrentPath <> '' then
      CurrentPath := CurrentPath + ';';

    CurrentPath := CurrentPath + InstallPath;
  end;

  if RegWriteExpandStringValue(
    HKEY_LOCAL_MACHINE,
    EnvironmentKey,
    'Path',
    CurrentPath
  ) then
    Log('PATH do sistema atualizado.')
  else
    Log('Erro ao atualizar PATH do sistema.');
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
    ModifySystemPath(False);
end;

procedure CurUninstallStepChanged(CurUninstallStep: TUninstallStep);
begin
  if CurUninstallStep = usUninstall then
    ModifySystemPath(True);
end;