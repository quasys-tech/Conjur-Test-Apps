FROM amd64/ruby:3.2.2 as test-app-builder
LABEL builder="test-app-builder"

#---some useful tools for interactive usage---#
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl

#---install summon and summon-conjur---#
RUN curl -sSL https://raw.githubusercontent.com/cyberark/summon/master/install.sh \
      | env TMPDIR=$(mktemp -d) bash && \
    curl -sSL https://raw.githubusercontent.com/cyberark/summon-conjur/master/install.sh \
      | env TMPDIR=$(mktemp -d) bash
# as per https://github.com/cyberark/summon#linux
# and    https://github.com/cyberark/summon-conjur#install
ENV PATH="/usr/local/lib/summon:${PATH}"

# ============= MAIN CONTAINER ============== #

FROM python:3.11-alpine
ARG namespace

#---copy summon into image---#
COPY --from=test-app-builder /usr/local/lib/summon /usr/local/lib/summon
COPY --from=test-app-builder /usr/local/bin/summon /usr/local/bin/summon

#---copy secrets.yml into image---#
COPY secrets.yml /etc/secrets.yml
COPY app.py /etc/app.py

#---override entrypoint to wrap command with summon---#
ENTRYPOINT [ "summon", "--provider", "summon-conjur", "-f", "/etc/secrets.yml", "python", "/etc/app.py"]