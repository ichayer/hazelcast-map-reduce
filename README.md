
<div style="display: flex; justify-content: space-between;">
  <div>
    <img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white">
    <img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white">
  </div>

  <div>
    <img src="https://github.com/ichayer/park-sync-booking/actions/workflows/maven.yml/badge.svg" alt="Java CI with Maven">
  </div>
</div>

# Authors
- [Camila Di Toro](https://github.com/camilaDiToro)
- [Iván Chayer](https://github.com/ichayer)
- [Thomas Mizrahi](https://github.com/ThomasMiz)

# Instalation
Run this command to compile the project with `Maven`:
```bash
mvn clean install
```
Extract `tar.gz` from `target` folders:
```bash
mkdir -p tmp/csv && find . -name '*tar.gz' -exec tar -C tmp -xzf {} \;
find . -path './tmp/tpe2-g4-*/*' -exec chmod u+x {} \;
```
This will extract the generated `.tar.gz` files, storing them in a temporary directory `./tmp`, and grant execution permissions to both the client and server `.sh` files. It will also create a `csv` folder to place CSVs in it.

Sample scripts are provided in the `scripts` folder. Update script flags values to match your environment.

> Note: these scripts MUST be executed from the root of the project and they must use absolute paths. An easy way to do this is replacing `"./output.txt"` with `"$PWD/output.txt"`
