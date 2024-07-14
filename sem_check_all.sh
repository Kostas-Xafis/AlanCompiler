#/bin/bash
find ./test_files/ -name *.alan | xargs -I @ sh -c 'echo "Semantic analysis for" @ ; java -jar target/compiler-0.0.2.jar -f @ -t ;'
