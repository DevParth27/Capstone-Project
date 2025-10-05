# Barcode and QR Code Reader Application

A comprehensive Java application for reading barcodes and QR codes from image files and webcam feeds.

## Features

- **File-based scanning**: Read barcodes from image files (JPG, PNG, BMP, GIF)
- **Real-time webcam scanning**: Live barcode detection using your computer's camera
- **Multiple format support**: Supports all major barcode formats including:
  - QR Code
  - Data Matrix
  - PDF417
  - Code 128
  - Code 39
  - EAN-13/EAN-8
  - UPC-A/UPC-E
  - And many more!
- **Batch processing**: Detect multiple barcodes in a single image
- **Continuous scanning**: Hands-free continuous webcam scanning mode
- **User-friendly GUI**: Clean tabbed interface with detailed results

## Requirements

- Java 11 or higher
- Maven (for automatic compilation) or manual JAR file setup
- Webcam (for real-time scanning features)

## Quick Start

### Method 1: Using Maven (Recommended)

1. Ensure Maven is installed and in your PATH
2. Run the batch file:
   ```
   compile_and_run.bat
   ```

### Method 2: Manual Compilation

1. Download the required JAR files and place them in the `lib/` folder:
   - `core-3.5.1.jar` (ZXing Core)
   - `javase-3.5.1.jar` (ZXing JavaSE)
   - `webcam-capture-0.3.12.jar` (Webcam Capture)
   - `slf4j-api-1.7.36.jar` (SLF4J API)
   - `slf4j-simple-1.7.36.jar` (SLF4J Simple)
   - `bridj-0.7.0.jar` (BridJ - required for webcam)

2. Run the manual compilation script:
   ```
   compile_manual.bat
   ```

## Usage

### File Reader Tab
1. Click "Select Image File" to choose an image containing barcodes
2. Use "Read Single Barcode" to detect the first barcode found
3. Use "Read Multiple Barcodes" to detect all barcodes in the image
4. View results in the text area below

### Webcam Reader Tab
1. Select your webcam from the dropdown menu
2. Click "Start Webcam" to activate the camera feed
3. Use "Capture & Scan" for single image captures and scanning
4. Use "Start Continuous Scan" for automatic real-time scanning
5. Remember to stop the webcam when finished

## Supported File Formats

**Input Images**: JPG, JPEG, PNG, BMP, GIF

**Barcode Formats**: All formats supported by ZXing library including:
- AZTEC
- CODABAR
- CODE_39
- CODE_93
- CODE_128
- DATA_MATRIX
- EAN_8
- EAN_13
- ITF
- MAXICODE
- PDF_417
- QR_CODE
- RSS_14
- RSS_EXPANDED
- UPC_A
- UPC_E
- UPC_EAN_EXTENSION

## Project Structure

```
src/main/java/com/barcodereader/
├── BarcodeReaderApp.java      # Main application class
├── QRCodeUtil.java            # Barcode reading utility
├── FileReaderPanel.java       # File-based scanning panel
└── WebcamReaderPanel.java     # Webcam-based scanning panel

lib/                           # External JAR dependencies
target/                        # Compiled classes and JAR output
pom.xml                       # Maven configuration
compile_and_run.bat           # Automatic build and run script
compile_manual.bat            # Manual compilation script
README.md                     # This file
```

## Dependencies

- **ZXing (Zebra Crossing)**: Core barcode processing library
- **Webcam Capture**: Java library for webcam access
- **SLF4J**: Logging framework
- **Java Swing**: GUI framework (built into Java)

## Troubleshooting

### Common Issues

1. **"Maven is not found in PATH"**
   - Install Maven from https://maven.apache.org/
   - Or use the manual compilation method

2. **"No webcams found"**
   - Ensure your webcam is connected and not being used by another application
   - Check Windows Device Manager for camera status
   - Try restarting the application

3. **"Unable to read image file"**
   - Ensure the image file is not corrupted
   - Try a different image format
   - Check that the file path doesn't contain special characters

4. **"No barcode/QR code found"**
   - Ensure the barcode is clearly visible and not blurry
   - Try adjusting lighting conditions
   - Ensure the barcode is not too small or too large in the image

### Performance Tips

- Use good lighting for webcam scanning
- Hold barcodes steady and at a readable distance
- For file scanning, use high-resolution, clear images
- Close other applications using the webcam before starting

## Development

To modify or extend the application:

1. The main application logic is in `BarcodeReaderApp.java`
2. Barcode reading functionality is centralized in `QRCodeUtil.java`
3. Each tab has its own panel class for modularity
4. Maven handles dependency management automatically

## License

This project is developed for educational purposes. Please check the licenses of the included libraries:
- ZXing: Apache License 2.0
- Webcam Capture: MIT License
- SLF4J: MIT License

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Verify all dependencies are correctly installed
3. Ensure Java version compatibility (Java 11+)

---

**Version**: 1.0.0  
**Author**: Capstone Project  
**Last Updated**: October 2025