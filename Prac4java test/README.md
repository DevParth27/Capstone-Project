run:
javac -cp "lib\*" -d target\classes -encoding UTF-8 src\main\java\com\barcodereader\SimpleBarcodeReader.java


java -cp "target\classes;lib\*" com.barcodereader.SimpleBarcodeReader