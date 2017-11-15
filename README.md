## Usage

- install Docker and docker-compose.
- download a Yellowfin installation jar and place in `yellowfin` folder.
- `docker-compose up -d`.

## Caveats

- uses an unsecured MySQL for persistence.
- mounts the entire Yellowfin app installation on host at `yellowfin_installation`.