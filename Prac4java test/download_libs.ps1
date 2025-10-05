# PowerShell script to download ZXing libraries for full barcode functionality
Write-Host "=============================================="
Write-Host "ZXING LIBRARY DOWNLOADER FOR BARCODE READER"
Write-Host "=============================================="
Write-Host ""

# Create lib directory if it doesn't exist
$libDir = "lib"
if (!(Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir
    Write-Host "✅ Created lib directory"
} else {
    Write-Host "✅ lib directory exists"
}

# Define the JAR files to download
$jarFiles = @(
    @{
        Name = "core-3.5.1.jar"
        Url = "https://repo1.maven.org/maven2/com/google/zxing/core/3.5.1/core-3.5.1.jar"
        Description = "ZXing Core Library"
    },
    @{
        Name = "javase-3.5.1.jar"
        Url = "https://repo1.maven.org/maven2/com/google/zxing/javase/3.5.1/javase-3.5.1.jar"
        Description = "ZXing JavaSE Extensions"
    },
    @{
        Name = "webcam-capture-0.3.12.jar"
        Url = "https://repo1.maven.org/maven2/com/github/sarxos/webcam-capture/0.3.12/webcam-capture-0.3.12.jar"
        Description = "Webcam Capture Library"
    },
    @{
        Name = "slf4j-api-1.7.36.jar"
        Url = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"
        Description = "SLF4J API"
    },
    @{
        Name = "slf4j-simple-1.7.36.jar"
        Url = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/1.7.36/slf4j-simple-1.7.36.jar"
        Description = "SLF4J Simple Implementation"
    },
    @{
        Name = "bridj-0.7.0.jar"
        Url = "https://repo1.maven.org/maven2/com/nativelibs4java/bridj/0.7.0/bridj-0.7.0.jar"
        Description = "BridJ Native Library Bridge"
    }
)

Write-Host "Downloading required JAR files..."
Write-Host ""

$downloadedCount = 0
$totalCount = $jarFiles.Count

foreach ($jar in $jarFiles) {
    $filePath = Join-Path $libDir $jar.Name
    
    if (Test-Path $filePath) {
        Write-Host "⏭️  Skipping $($jar.Name) - already exists"
    } else {
        Write-Host "⬇️  Downloading $($jar.Description)..."
        try {
            Invoke-WebRequest -Uri $jar.Url -OutFile $filePath -UseBasicParsing
            if (Test-Path $filePath) {
                $size = (Get-Item $filePath).Length
                Write-Host "   ✅ Downloaded $($jar.Name) ($([math]::Round($size/1KB, 1)) KB)"
                $downloadedCount++
            } else {
                Write-Host "   ❌ Failed to download $($jar.Name)"
            }
        } catch {
            Write-Host "   ❌ Error downloading $($jar.Name): $($_.Exception.Message)"
        }
    }
}

Write-Host ""
Write-Host "Download Summary:"
Write-Host "=================="
Write-Host "Successfully downloaded: $downloadedCount/$totalCount JAR files"

if ($downloadedCount -eq $totalCount) {
    Write-Host "🎉 ALL LIBRARIES DOWNLOADED SUCCESSFULLY!"
    Write-Host ""
    Write-Host "Now you can run the full version with:"
    Write-Host "  .\run_full_with_libs.bat"
    Write-Host ""
    Write-Host "Or compile manually with:"
    Write-Host "  javac -cp `"lib\*`" -d target\classes src\main\java\com\barcodereader\FullBarcodeReaderApp.java"
    Write-Host "  java -cp `"target\classes;lib\*`" com.barcodereader.FullBarcodeReaderApp"
} else {
    Write-Host "⚠️  Some downloads failed. Check your internet connection and try again."
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")