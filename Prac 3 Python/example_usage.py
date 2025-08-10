from phishing_detector import PhishingDetector
from dataset_handler import DatasetHandler

def main():
    print("Phishing Website Detection Demo")
    print("=" * 40)
    
    # Load dataset
    handler = DatasetHandler()
    dataset = handler.load_dataset('phishing_dataset.csv')
    handler.dataset_info()
    
    # Get URLs and labels
    urls, labels = handler.get_urls_and_labels()
    
    # Create and train detector
    detector = PhishingDetector()
    print("\nTraining model...")
    accuracy = detector.train(urls, labels)
    
    # Interactive testing
    print("\nInteractive URL Testing")
    print("Enter 'quit' to exit")
    print("-" * 30)
    
    while True:
        url = input("Enter URL to check: ").strip()
        if url.lower() == 'quit':
            break
        
        if url:
            result = detector.predict(url)
            print(f"Prediction: {result['prediction']}")
            print(f"Confidence: {result['confidence']:.4f}")
            print("-" * 30)

if __name__ == "__main__":
    main()