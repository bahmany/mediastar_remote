#include <iostream>
#include <string>

int main(int argc, char** argv) {
    std::cout << "Test program started\n";
    std::cout << "argc: " << argc << "\n";
    for (int i = 0; i < argc; i++) {
        std::cout << "argv[" << i << "]: " << argv[i] << "\n";
    }
    return 0;
}
