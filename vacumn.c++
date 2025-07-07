#include <bits/stdc++.h>
using namespace std;

class VacuumCleaner {
private:
    vector<vector<int>> room;
    vector<vector<bool>> visited;
    int rows, cols;
    int currentX, currentY;
    int dirtCleaned;
    int dx[4] = {-1, 0, 1, 0};
    int dy[4] = {0, 1, 0, -1};
    
public:
    VacuumCleaner(int r, int c) : rows(r), cols(c), currentX(0), currentY(0), dirtCleaned(0) {
        room.resize(rows, vector<int>(cols, 0));
        visited.resize(rows, vector<bool>(cols, false));
    }
    
    void setDirt(int x, int y) {
        if (x >= 0 && x < rows && y >= 0 && y < cols) {
            room[x][y] = 1;
        }
    }
    void displayRoom() {
        cout << "\nRoom Layout:\n";
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == currentX && j == currentY) {
                    cout << "V ";
                } else {
                    cout << room[i][j] << " ";
                }
            }
            cout << endl;
        }
        cout << endl;
    }
    bool isValid(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < cols;
    }   
    void clean() {
        cout << "Vacuum cleaner starting at position (" << currentX << ", " << currentY << ")\n";
        displayRoom(); 
        dfsClean(currentX, currentY);     
        cout << "Cleaning complete! Total dirt cleaned: " << dirtCleaned << endl;
        displayRoom();
    }
    void dfsClean(int x, int y) {
        visited[x][y] = true;
        if (room[x][y] == 1) {
            cout << "Found dirt at (" << x << ", " << y << ") - Cleaning...\n";
            room[x][y] = 0;
            dirtCleaned++;
        } else {
            cout << "Position (" << x << ", " << y << ") is already clean.\n";
        }
        currentX = x;
        currentY = y;
        for (int i = 0; i < 4; i++) {
            int newX = x + dx[i];
            int newY = y + dy[i];
            if (isValid(newX, newY) && !visited[newX][newY]) {
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
};
int main() {
    cout << "Vacuum Cleaner Agent Simulation\n";
    VacuumCleaner vacuum(5, 5);
    vacuum.setDirt(1, 1);
    vacuum.setDirt(2, 3);
    vacuum.setDirt(3, 2);
    vacuum.setDirt(4, 4);
    vacuum.setDirt(0, 3); 
    cout << "Initial dirt count: " << vacuum.getTotalDirt() << endl;
    vacuum.clean();   
    cout << "Final dirt count: " << vacuum.getTotalDirt() << endl;
    return 0;
}