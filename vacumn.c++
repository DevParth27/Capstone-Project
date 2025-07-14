#include <bits/stdc++.h>
using namespace std;

struct Point {
    int x, y;
    Point(int x = 0, int y = 0) : x(x), y(y) {}
};

class VacuumCleaner {
private:
    vector<vector<int>> room;  
    vector<vector<bool>> visited;
    int rows, cols;
    int currentX, currentY;
    int dirtCleaned;
    int dx[4] = {-1, 0, 1, 0};
    int dy[4] = {0, 1, 0, -1};
    
    // Colors for console output
    const string RESET = "\033[0m";
    const string RED = "\033[31m";      
    const string GREEN = "\033[32m";
    const string BLUE = "\033[34m";
    const string YELLOW = "\033[33m";  
    const string CYAN = "\033[36m";     
    
public:
    VacuumCleaner(int r, int c) : rows(r), cols(c), currentX(0), currentY(0), dirtCleaned(0) {
        room.resize(rows, vector<int>(cols, 0));
        visited.resize(rows, vector<bool>(cols, false));
    }
    
    void setDirt(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < cols && room[x][y] == 0) {
            room[x][y] = 1;
        }
    }
    
    void setObstacle(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < cols) {
            room[x][y] = 2;
        }
    }
    
    // Polygon filling algorithm - Scan line fill
    void fillPolygon(vector<Point>& polygon) {
        if (polygon.size() < 3) return;
        
        // Find min and max Y coordinates
        int minY = polygon[0].y, maxY = polygon[0].y;
        for (const auto& p : polygon) {
            minY = min(minY, p.y);
            maxY = max(maxY, p.y);
        }
        
        // For each scan line
        for (int y = minY; y <= maxY; y++) {
            vector<int> intersections;
            
            // Find intersections with polygon edges
            for (int i = 0; i < polygon.size(); i++) {
                int j = (i + 1) % polygon.size();
                Point p1 = polygon[i], p2 = polygon[j];
                
                if ((p1.y <= y && p2.y > y) || (p2.y <= y && p1.y > y)) {
                    int x = p1.x + (y - p1.y) * (p2.x - p1.x) / (p2.y - p1.y);
                    intersections.push_back(x);
                }
            }
            
            sort(intersections.begin(), intersections.end());
            
            for (int i = 0; i < intersections.size(); i += 2) {
                if (i + 1 < intersections.size()) {
                    for (int x = intersections[i]; x <= intersections[i + 1]; x++) {
                        setObstacle(x, y);
                    }
                }
            }
        }
    }
    
    void createRectangularObstacle(int x1, int y1, int x2, int y2) {
        vector<Point> rectangle = {
            Point(x1, y1), Point(x2, y1), 
            Point(x2, y2), Point(x1, y2)
        };
        fillPolygon(rectangle);
    }
    
    void createTriangularObstacle(int x1, int y1, int x2, int y2, int x3, int y3) {
        vector<Point> triangle = {
            Point(x1, y1), Point(x2, y2), Point(x3, y3)
        };
        fillPolygon(triangle);
    }
    void displayRoom() {
        cout << "\nRoom Layout:\n";
        cout << "Legend: V=Vacuum, #=Obstacle, *=Dirt, .=Clean, o=Cleaned\n";
        
        // Top border
        cout << "+";
        for (int j = 0; j < cols; j++) cout << "--";
        cout << "+\n";
        
        for (int i = 0; i < rows; i++) {
            cout << "|"; 
            for (int j = 0; j < cols; j++) {
                if (i == currentX && j == currentY) {
                    cout << GREEN << "V " << RESET;
                } else {
                    switch (room[i][j]) {
                        case 0: cout << ". "; break; 
                        case 1: cout << RED << "* " << RESET; break;
                        case 2: cout << YELLOW << "# " << RESET; break; 
                        case 3: cout << CYAN << "o " << RESET; break; 
                        default: cout << ". "; break;
                    }
                }
            }
            cout << "|"; 
            cout << endl;
        }
        
    
        cout << "+";
        for (int j = 0; j < cols; j++) cout << "--";
        cout << "+\n";
        cout << endl;
    }
    
    void displayRoomASCII() {
        cout << "\nASCII Room Layout:\n";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == currentX && j == currentY) {
                    cout << "V ";
                } else {
                    switch (room[i][j]) {
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
    bool isValid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols && room[x][y] != 2; // Can't move through obstacles
    }
    
    bool canMoveTo(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols && room[x][y] != 2 && !visited[x][y];
    }   
    void clean() {
        cout << "Vacuum cleaner starting at position (" << currentX << ", " << currentY << ")\n";
        displayRoom(); 
        
        // Check if starting position is valid
        if (room[currentX][currentY] == 2) {
            cout << "Error: Starting position is blocked by obstacle!\n";
            return;
        }
        
        dfsClean(currentX, currentY);     
        cout << "Cleaning complete! Total dirt cleaned: " << dirtCleaned << endl;
        cout << "Final room state:\n";
        displayRoom();
    }
    void dfsClean(int x, int y) {
        visited[x][y] = true;
        
        if (room[x][y] == 1) {
            cout << "Found dirt at (" << x << ", " << y << ") - Cleaning...\n";
            room[x][y] = 3; // Mark as cleaned area
            dirtCleaned++;
        } else if (room[x][y] == 0) {
            cout << "Position (" << x << ", " << y << ") is already clean.\n";
            room[x][y] = 3; // Mark as visited clean area
        }
        
        currentX = x;
        currentY = y;
        
        // Show current state after each move
        displayRoomASCII();
        
        for (int i = 0; i < 4; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            
            if (canMoveTo(newX, newY)) {
                cout << "Moving to position (" << newX << ", " << newY << ")\n";
                dfsClean(newX, newY);
            }
        }
    }
    int getTotalDirt() {
        int total = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (room[i][j] == 1) total++;
            }
        }
        return total;
    }
    
    int getTotalObstacles() {
        int total = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (room[i][j] == 2) total++;
            }
        }
        return total;
    }
    
    void resetVisited() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                visited[i][j] = false;
            }
        }
    }
    
    void setStartPosition(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < cols && room[x][y] != 2) {
            currentX = x;
            currentY = y;
        }
    }
};
int main() {
    
    // Create a larger room for better demonstration
    VacuumCleaner vacuum(8, 10);
    
    // Set starting position
    vacuum.setStartPosition(0, 0);
    
    // Create obstacles using polygon filling
    cout << "Creating obstacles using polygon filling algorithms...\n";
    
    // Create a rectangular obstacle (furniture)
    vacuum.createRectangularObstacle(2, 2, 4, 4);
    cout << "Created rectangular obstacle (furniture)\n";
    
    // Create a triangular obstacle (corner furniture)
    vacuum.createTriangularObstacle(6, 1, 7, 1, 6, 3);
    cout << "Created triangular obstacle (corner furniture)\n";
    
    // Create individual obstacles (walls/pillars)
    vacuum.setObstacle(1, 6);
    vacuum.setObstacle(1, 7);
    vacuum.setObstacle(2, 6);
    vacuum.setObstacle(2, 7);
    cout << "Created wall obstacles\n";
    
    // Create another triangular obstacle
    vacuum.createTriangularObstacle(5, 6, 6, 8, 7, 6);
    cout << "Created another triangular obstacle\n";
    
    // Add dirt in various locations
    cout << "\nAdding dirt to the room...\n";
    vacuum.setDirt(0, 5);
    vacuum.setDirt(1, 1);
    vacuum.setDirt(1, 9);
    vacuum.setDirt(3, 0);
    vacuum.setDirt(3, 8);
    vacuum.setDirt(4, 1);
    vacuum.setDirt(4, 9);
    vacuum.setDirt(5, 0);
    vacuum.setDirt(5, 2);
    vacuum.setDirt(6, 9);
    vacuum.setDirt(7, 0);
    vacuum.setDirt(7, 5);
    vacuum.setDirt(7, 9);
    
    cout << "Initial dirt count: " << vacuum.getTotalDirt() << endl;
    cout << "Total obstacles: " << vacuum.getTotalObstacles() << endl;
    
    cout << "\nStarting cleaning process...\n";
    vacuum.clean();   
    
    cout << "Final dirt count: " << vacuum.getTotalDirt() << endl;
    
    if (vacuum.getTotalDirt() == 0) {
        cout << "\n All accessible dirt has been cleaned!\n";
    } else {
        cout << "\n Some dirt may be unreachable due to obstacles.\n";
    }
    
    return 0;
}