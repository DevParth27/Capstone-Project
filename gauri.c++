#include <bits/stdc++.h>
using namespace std;

struct Coordinate {
    int xPos, yPos;
    Coordinate(int x = 0, int y = 0) : xPos(x), yPos(y) {}
};

class SmartVacuum {
private:
    vector<vector<int>> grid;  
    vector<vector<bool>> explored;
    int height, width;
    int robotX, robotY;
    int cleanedDirt;
    int moveX[4] = {-1, 0, 1, 0};
    int moveY[4] = {0, 1, 0, -1};
    
    // Color codes for terminal display
    const string RESET_COLOR = "\033[0m";
    const string RED_COLOR = "\033[31m";      
    const string GREEN_COLOR = "\033[32m";
    const string BLUE_COLOR = "\033[34m";
    const string YELLOW_COLOR = "\033[33m";  
    const string CYAN_COLOR = "\033[36m";     
    
public:
    SmartVacuum(int h, int w) : height(h), width(w), robotX(0), robotY(0), cleanedDirt(0) {
        grid.resize(height, vector<int>(width, 0));
        explored.resize(height, vector<bool>(width, false));
    }
    
    void placeDirt(int x, int y) {
        if (x >= 0 && x < height && y >= 0 && y < width && grid[x][y] == 0) {
            grid[x][y] = 1;
        }
    }
    
    void placeBarrier(int x, int y) {
        if (x >= 0 && x < height && y >= 0 && y < width) {
            grid[x][y] = 2;
        }
    }
    
    // Polygon filling algorithm - Scan line fill
    void fillPolygon(vector<Coordinate>& polygon) {
        if (polygon.size() < 3) return;
        
        // Find min and max Y coordinates
        int minY = polygon[0].yPos, maxY = polygon[0].yPos;
        for (const auto& p : polygon) {
            minY = min(minY, p.yPos);
            maxY = max(maxY, p.yPos);
        }
        
        // For each scan line
        for (int y = minY; y <= maxY; y++) {
            vector<int> intersections;
            
            // Find intersections with polygon edges
            for (int i = 0; i < polygon.size(); i++) {
                int j = (i + 1) % polygon.size();
                Coordinate p1 = polygon[i], p2 = polygon[j];
                
                if ((p1.yPos <= y && p2.yPos > y) || (p2.yPos <= y && p1.yPos > y)) {
                    int x = p1.xPos + (y - p1.yPos) * (p2.xPos - p1.xPos) / (p2.yPos - p1.yPos);
                    intersections.push_back(x);
                }
            }
            
            sort(intersections.begin(), intersections.end());
            
            for (int i = 0; i < intersections.size(); i += 2) {
                if (i + 1 < intersections.size()) {
                    for (int x = intersections[i]; x <= intersections[i + 1]; x++) {
                        placeBarrier(x, y);
                    }
                }
            }
        }
    }
    
    void createRectangularObstacle(int x1, int y1, int x2, int y2) {
        vector<Coordinate> rectangle = {
            Coordinate(x1, y1), Coordinate(x2, y1), 
            Coordinate(x2, y2), Coordinate(x1, y2)
        };
        fillPolygon(rectangle);
    }
    
    void createTriangularObstacle(int x1, int y1, int x2, int y2, int x3, int y3) {
        vector<Coordinate> triangle = {
            Coordinate(x1, y1), Coordinate(x2, y2), Coordinate(x3, y3)
        };
        fillPolygon(triangle);
    }
    void showRoom() {
        cout << "\nRoom Layout:\n";
        cout << "Legend: V=Vacuum, #=Obstacle, *=Dirt, .=Clean, o=Cleaned\n";
        
        // Top border
        cout << "+";
        for (int j = 0; j < width; j++) cout << "--";
        cout << "+\n";
        
        for (int i = 0; i < height; i++) {
            cout << "|"; 
            for (int j = 0; j < width; j++) {
                if (i == robotX && j == robotY) {
                    cout << GREEN_COLOR << "V " << RESET_COLOR;
                } else {
                    switch (grid[i][j]) {
                        case 0: cout << ". "; break; 
                        case 1: cout << RED_COLOR << "* " << RESET_COLOR; break;
                        case 2: cout << YELLOW_COLOR << "# " << RESET_COLOR; break; 
                        case 3: cout << CYAN_COLOR << "o " << RESET_COLOR; break; 
                        default: cout << ". "; break;
                    }
                }
            }
            cout << "|"; 
            cout << endl;
        }
        
    
        cout << "+";
        for (int j = 0; j < width; j++) cout << "--";
        cout << "+\n";
        cout << endl;
    }
    
    void showRoomASCII() {
        cout << "\nASCII Room Layout:\n";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (i == robotX && j == robotY) {
                    cout << "V ";
                } else {
                    switch (grid[i][j]) {
                        case 0: cout << ". "; break;  // Clean
                        case 1: cout << "* "; break;  // Dirt
                        case 2: cout << "# "; break;  // Obstacle
                        case 3: cout << "o "; break;  // Cleaned area
                        default: cout << ". "; break;
                    }
                }
            }
            cout << endl;
        }
        cout << endl;
    }
    bool isValidPosition(int x, int y) {
        return x >= 0 && x < height && y >= 0 && y < width && grid[x][y] != 2; // Can't move through obstacles
    }
    
    bool canMoveToPosition(int x, int y) {
        return x >= 0 && x < height && y >= 0 && y < width && grid[x][y] != 2 && !explored[x][y];
    }   
    void startCleaning() {
        cout << "Vacuum cleaner starting at position (" << robotX << ", " << robotY << ")\n";
        showRoom(); 
        
        // Check if starting position is valid
        if (grid[robotX][robotY] == 2) {
            cout << "Error: Starting position is blocked by obstacle!\n";
            return;
        }
        
        dfsCleaningProcess(robotX, robotY);     
        cout << "Cleaning complete! Total dirt cleaned: " << cleanedDirt << endl;
        cout << "Final room state:\n";
        showRoom();
    }
    void dfsCleaningProcess(int x, int y) {
        explored[x][y] = true;
        
        if (grid[x][y] == 1) {
            cout << "Found dirt at (" << x << ", " << y << ") - Cleaning...\n";
            grid[x][y] = 3; // Mark as cleaned area
            cleanedDirt++;
        } else if (grid[x][y] == 0) {
            cout << "Position (" << x << ", " << y << ") is already clean.\n";
            grid[x][y] = 3; // Mark as visited clean area
        }
        
        robotX = x;
        robotY = y;
        
        // Show current state after each move
        showRoomASCII();
        
        for (int i = 0; i < 4; i++) {
            int newX = x + moveX[i];
            int newY = y + moveY[i];
            
            if (canMoveToPosition(newX, newY)) {
                cout << "Moving to position (" << newX << ", " << newY << ")\n";
                dfsCleaningProcess(newX, newY);
            }
        }
    }
    int countTotalDirt() {
        int total = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == 1) total++;
            }
        }
        return total;
    }
    
    int countTotalObstacles() {
        int total = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == 2) total++;
            }
        }
        return total;
    }
    
    void resetExploredAreas() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                explored[i][j] = false;
            }
        }
    }
    
    void setRobotPosition(int x, int y) {
        if (x >= 0 && x < height && y >= 0 && y < width && grid[x][y] != 2) {
            robotX = x;
            robotY = y;
        }
    }
};
int main() {
    
    // Create a larger room for better demonstration
    SmartVacuum smartCleaner(8, 10);
    
    // Set starting position
    smartCleaner.setRobotPosition(0, 0);
    
    // Create obstacles using polygon filling
    cout << "Creating obstacles using polygon filling algorithms...\n";
    
    // Create a rectangular obstacle (furniture)
    smartCleaner.createRectangularObstacle(2, 2, 4, 4);
    cout << "Created rectangular obstacle (furniture)\n";
    
    // Create a triangular obstacle (corner furniture)
    smartCleaner.createTriangularObstacle(6, 1, 7, 1, 6, 3);
    cout << "Created triangular obstacle (corner furniture)\n";
    
    // Create individual obstacles (walls/pillars)
    smartCleaner.placeBarrier(1, 6);
    smartCleaner.placeBarrier(1, 7);
    smartCleaner.placeBarrier(2, 6);
    smartCleaner.placeBarrier(2, 7);
    cout << "Created wall obstacles\n";
    
    // Create another triangular obstacle
    smartCleaner.createTriangularObstacle(5, 6, 6, 8, 7, 6);
    cout << "Created another triangular obstacle\n";
    
    // Add dirt in various locations
    cout << "\nAdding dirt to the room...\n";
    smartCleaner.placeDirt(0, 5);
    smartCleaner.placeDirt(1, 1);
    smartCleaner.placeDirt(1, 9);
    smartCleaner.placeDirt(3, 0);
    smartCleaner.placeDirt(3, 8);
    smartCleaner.placeDirt(4, 1);
    smartCleaner.placeDirt(4, 9);
    smartCleaner.placeDirt(5, 0);
    smartCleaner.placeDirt(5, 2);
    smartCleaner.placeDirt(6, 9);
    smartCleaner.placeDirt(7, 0);
    smartCleaner.placeDirt(7, 5);
    smartCleaner.placeDirt(7, 9);
    
    cout << "Initial dirt count: " << smartCleaner.countTotalDirt() << endl;
    cout << "Total obstacles: " << smartCleaner.countTotalObstacles() << endl;
    
    cout << "\nStarting cleaning process...\n";
    smartCleaner.startCleaning();   
    
    cout << "Final dirt count: " << smartCleaner.countTotalDirt() << endl;
    
    if (smartCleaner.countTotalDirt() == 0) {
        cout << "\n All accessible dirt has been cleaned!\n";
    } else {
        cout << "\n Some dirt may be unreachable due to obstacles.\n";
    }
    
    return 0;
}