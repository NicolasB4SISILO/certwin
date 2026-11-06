# CertWin

Aplicação de linha de comando que emite certificados TLS do [Let's Encrypt](https://letsencrypt.org/) no Windows.

## Por que existe

A ideia veio de um problema real: o Certbot, usado para gerar certificados TLS, foi [descontinuado para Windows](https://community.letsencrypt.org/t/certbot-discontinuing-windows-beta-support-in-2024/208101). Por necessidade, criei o CertWin para usar no lugar dele. Já o utilizo e funciona bem.

## Requisitos

Para emitir certificados, o domínio precisa apontar para a máquina e a porta 80 precisa estar acessível pela internet e livre durante a execução.

## Instalação

Execute o `certwin-setup.exe` como administrador. Ele já inclui o runtime Java e adiciona o `certwin` ao `PATH`.

## Uso

```powershell
# Emitir o certificado em PEM
certwin -C --email=ti@exemplo.com.br --dominio=exemplo.com.br

# Emitir e gerar também o .p12
certwin -CP --email=ti@exemplo.com.br --dominio=exemplo.com.br --senha=SuaSenha --alias=minhaapp

# Converter arquivos PEM existentes para .p12
certwin -P --senha=SuaSenha --alias=minhaapp --entrada=C:\caminho\dos\pem
```

Use `--staging` para testar sem emitir um certificado válido e `--saida` para escolher o caminho do `.p12`. Todas as opções estão em `certwin --help`.

Os arquivos são gravados na pasta da aplicação (`C:\Program Files\Certwin\app`, com o instalador):

- `certs/<dominio>/pem/`: `privkey.pem`, `cert.pem`, `chain.pem` e `fullchain.pem`
- `certs/<dominio>/p12/`: `.p12` gerado com `-CP`
- `converts/`: `.p12` gerado com `-P`

## Limitações

- A renovação não é automática: para renovar, execute a emissão novamente.
- Cada certificado cobre um único domínio, sem wildcard.
- A validação usa apenas HTTP-01, na porta 80.

## Build

Requer JDK 21 e Maven.

```powershell
mvn clean package
```

O JAR é gerado em `target\certwin.jar`. Para gerar o instalador, veja [docs/Build.md](docs/Build.md).

Feito com Java 21, [acme4j](https://acme4j.shredzone.org/), [Bouncy Castle](https://www.bouncycastle.org/) e [picocli](https://picocli.info/).
