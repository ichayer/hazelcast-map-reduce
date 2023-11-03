
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
find tmp -type d -name 'tpe2-g4-*' -exec cp config.json {} \;
```

This will extract the generated `.tar.gz` files, storing them in a temporary directory `./tmp`, and grant execution permissions to both the client and server `.sh` files. It will also create a `csv` folder to place CSVs in it.

Sample scripts are provided in the `scripts` folder. Update script flags values to match your environment. These scripts expect the `bikes.csv` and `stations.csv` to be in the `./tmp/csv` folder.

> Note: these scripts MUST be executed from the root of the project.

### Example of execution
On one terminal, we run the server:

```bash
./scripts/run-server.sh
```

Then, on another terminal:
```bash
./scripts/query4.sh
```

The following scripts are provided:
- `./scripts/run-server.sh`
- `./scripts/query1.sh`
- `./scripts/query1Strategies/fromStations.sh`
- `./scripts/query2.sh`
- `./scripts/query2Strategies/fromStations.sh`
- `./scripts/query3.sh`
- `./scripts/query4.sh`
- `./scripts/query4Strategies/fromStations.sh`

These scripts have fixed parameters. For the query4, they all connect to `127.0.0.1:5071` and place the output files in the root of the project. The query 4 uses as date range 01/01/2021 to 31/12/2021, and the query 2 sets the limit N=4. If you want to reuse these scripts but run them with different parameters, you can modify them directly from these `./scripts/queryX.sh` files.
