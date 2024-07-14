#/bin/bash
echo $1 | xargs -I @ sh -c 'echo "Semantic analysis for" @ ; java -jar target/compiler-0.0.2.jar -f @ -t ;'