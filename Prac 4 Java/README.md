# Barcode and QR Code Reader

This Java application can read barcodes and QR codes from both image files and webcam input.
.\compile_and_run.bat
## Features

- Read barcodes and QR codes from image files
- Real-time barcode and QR code scanning from webcam
- Supports multiple barcode formats (QR, Code 128, EAN, UPC, etc.)
- Simple and intuitive user interface

## Requirements

- Java 11 or higher
- Maven for building the project
- Webcam for live scanning functionality

## How to Build

```bash
mvn clean package
```

This will create a runnable JAR file in the `target` directory.

## How to Run

```bash
java -jar target/barcode-qr-reader-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Or simply double-click the JAR file if your system is configured to run JAR files.

## Usage

### File Reader Tab

1. Click "Select Image" to choose an image file containing a barcode or QR code
2. The application will display the image and attempt to decode any barcode or QR code
3. Results will be shown in the text area below the image

### Webcam Reader Tab

1. Select your webcam from the dropdown list
2. Click "Start" to begin the webcam capture
3. Point your webcam at a barcode or QR code
4. When a code is detected, the result will be displayed in the text area
5. Click "Stop" to end the webcam capture

## Libraries Used

- ZXing ("Zebra Crossing") for barcode processing
- Webcam Capture for accessing webcam devices
- Swing for the user interface