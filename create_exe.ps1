# ============================================
#   FindClass - Generateur de .exe
#   Cree un fichier .exe qui lance le JAR
# ============================================

$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$distDir = Join-Path $projectDir "dist"
$projectName = "FindClass"
$jarFile = "$projectName.jar"
$exeName = "$projectName.exe"
$exePath = Join-Path $distDir $exeName

# Code C# pour creer un wrapper .exe
$csharpCode = @"
using System;
using System.Diagnostics;
using System.IO;

class Program {
    static void Main(string[] args) {
        string exeDir = AppDomain.CurrentDomain.BaseDirectory;
        string jarPath = Path.Combine(exeDir, "$jarFile");
        
        if (!File.Exists(jarPath)) {
            Console.WriteLine("ERREUR: $jarFile introuvable dans " + exeDir);
            Console.ReadKey();
            return;
        }
        
        ProcessStartInfo psi = new ProcessStartInfo();
        psi.FileName = "java";
        psi.Arguments = "-jar \"" + jarPath + "\"";
        psi.WorkingDirectory = exeDir;
        psi.UseShellExecute = false;
        
        try {
            Process p = Process.Start(psi);
            p.WaitForExit();
        } catch (Exception ex) {
            Console.WriteLine("ERREUR: " + ex.Message);
            Console.WriteLine("Verifiez que Java 17+ est installe.");
            Console.ReadKey();
        }
    }
}
"@

Write-Host ""
Write-Host "  ============================================" -ForegroundColor Cyan
Write-Host "    UniRoom - Creation du .exe" -ForegroundColor Cyan
Write-Host "  ============================================" -ForegroundColor Cyan
Write-Host ""

# Compiler le .exe avec le compilateur C# integre a Windows
$csharpFile = Join-Path $distDir "UniRoom.cs"
$csharpCode | Out-File -FilePath $csharpFile -Encoding UTF8

# Utiliser le compilateur C# de .NET Framework (toujours present sur Windows)
$cscPath = Join-Path ([System.Runtime.InteropServices.RuntimeEnvironment]::GetRuntimeDirectory()) "csc.exe"

if (Test-Path $cscPath) {
    Write-Host "  [1/2] Compilation du lanceur .exe..." -ForegroundColor Yellow
    & $cscPath /nologo /out:$exePath /target:winexe $csharpFile 2>&1 | Out-Null
    
    if (Test-Path $exePath) {
        Write-Host "  [OK] UniRoom.exe cree avec succes !" -ForegroundColor Green
    }
    else {
        Write-Host "  [WARN] Tentative alternative..." -ForegroundColor Yellow
        # Alternative: utiliser dotnet si disponible
        Add-Type -TypeDefinition $csharpCode -OutputAssembly $exePath -OutputType WindowsApplication 2>&1 | Out-Null
    }
}
else {
    Write-Host "  [INFO] Utilisation de Add-Type..." -ForegroundColor Yellow
    Add-Type -TypeDefinition $csharpCode -OutputAssembly $exePath -OutputType WindowsApplication
}

# Nettoyage
if (Test-Path $csharpFile) { Remove-Item $csharpFile }

Write-Host ""
if (Test-Path $exePath) {
    $size = [math]::Round((Get-Item $exePath).Length / 1024, 1)
    Write-Host "  ============================================" -ForegroundColor Green
    Write-Host "    UniRoom.exe cree ! ($size KB)" -ForegroundColor Green
    Write-Host "    Emplacement: $exePath" -ForegroundColor Green
    Write-Host "  ============================================" -ForegroundColor Green
}
else {
    Write-Host "  [ERREUR] Impossible de creer le .exe" -ForegroundColor Red
    Write-Host "  Utilisez UniRoom.bat comme alternative." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "  Appuyez sur une touche pour fermer..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
