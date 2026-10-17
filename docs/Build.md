# Build completo da aplicação

O build gera um instalador para Windows contendo a aplicação Java e um runtime customizado, dispensando a instalação prévia do Java.

## Etapas do build

1. **`build/build-image.sh`**: Utiliza o `jpackage` para gerar a imagem da aplicação (`app-image`), contendo o JAR e o runtime Java.
2. **`build/build-setup.iss`**: Utiliza o `Inno Setup` para gerar o instalador, configurar as variáveis de ambiente `CERTWIN_HOME` e `PATH`, e liberar permissões de escrita no diretório da aplicação.

## Observações

A permissão de escrita é necessária porque a aplicação cria a pasta `certs` durante a execução, inclusive quando instalada em diretórios restritos, como `Program Files`. Após a instalação, a aplicação pode ser executada pelo terminal usando `certwin`, seguido dos argumentos desejados.

**Exemplo**:
```
certwin --help
certwin -C --email=[EMAIL] --dominio=[DOMINIO]
certwin -P --senha=[SENHA] --alias=[ALIAS] --entrada=[ENTRADA]
certwin -CP --email=[EMAIL] --dominio=[DOMINIO] --senha=[SENHA] --alias=[ALIAS]
```