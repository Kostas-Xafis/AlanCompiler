# Alan Programming language compiler

## Get started

```bash
# Clone the project
git clone https://github.com/Kostas-Xafis/AlanCompiler.git

cd AlanCompiler

# Build the maven project
./build.sh

# Run the program to parse all the files in './test_file' directory
./syntax_check_all.sh

## Semantic Analysis ##

# Run the program to semantically check all the files in './test_file' directory
./sem_check_all.sh

# Run the program to semantically check an input file
./sem.sh ./test_files/hello_world.alan


## Code Generation ##

# Run the program to generate the intermediate code for all the files in './test_file' directory
./compile_all.sh

# Run the program to generate the intermediate code for an input file
./compile.sh ./test_files/hello_world.alan


## Execution ##

cd ./class_output

java hello_world
```
