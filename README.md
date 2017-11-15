## Usage

- install Docker and docker-compose.
- download a Yellowfin installation jar and place in `yellowfin` folder.
- edit `docker-compose.yml` and update `YELLOWFIN_VERSION` under `yellowfin` container.
- `docker-compose up -d`.

## Notes

- uses an unsecured MySQL for persistence.
- mounts the entire Yellowfin app installation on host at `yellowfin_installation`.
- MySQL will be listening on port 3306 on the host.
- Yellowfin will be listening on port 7900 on the host.
