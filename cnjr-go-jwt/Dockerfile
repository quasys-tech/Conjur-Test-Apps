FROM golang:alpine

WORKDIR /src

COPY go.mod ./
RUN go mod download

RUN go get github.com/cyberark/conjur-api-go/conjurapi

COPY main.go ./

RUN CGO_ENABLED=0 GOOS=linux GOARCH=amd64 go build -o /bin/app .

ENTRYPOINT [ "/bin/app" ]