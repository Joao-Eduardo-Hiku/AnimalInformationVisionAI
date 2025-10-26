Animal Information Vision AI

Descrição do projeto

O "Animal Information Vision AI" é um aplicativo Android nativo projetado para permitir que os usuários busquem informações detalhadas sobre animais.

O aplicativo oferece duas funcionalidades principais de busca:

1. Busca por Texto O usuário pode digitar o nome de um animal em um campo de busca. O aplicativo traduz o termo de português para inglês e, em seguida, busca as informações.
2. Busca por Imagem O usuário pode usar a câmera para tirar uma foto ou escolher uma imagem existente da galeria. O aplicativo usa inteligência artificial para identificar o animal na imagem e, em seguida, usa o nome identificado para realizar a busca.

Após a busca, o aplicativo exibe os resultados em uma tela dedicada, mostrando detalhes como nome, reino, filo, ordem, família, nome científico, locais e presas. O aplicativo também traduz alguns dos resultados (como nome e presas) de volta para o português para exibição.

O aplicativo também armazena um histórico das buscas realizadas, permitindo que o usuário clique em um item anterior para pesquisar novamente.

APIs utilizadas

Este projeto utiliza três APIs externas principais:

Google Cloud Vision AI (`vision.googleapis.com`)
Usada para a funcionalidade de reconhecimento de imagem. O aplicativo envia a imagem (capturada pela câmera ou selecionada da galeria) para a API e recebe rótulos de identificação (Label Detection). O termo de maior relevância é usado para a busca.

API Ninjas (`api.api-ninjas.com`)
Usada para obter as informações detalhadas sobre os animais. Após a identificação por texto ou imagem, o nome do animal (em inglês) é enviado ao endpoint `v1/animals` para recuperar dados de taxonomia e características.

MyMemory Translation (`api.mymemory.translated.net`)
Utilizada para as traduções de idiomas. Ela é usada em dois momentos:
1.  Para traduzir o termo de busca digitado pelo usuário de português para inglês (antes de consultar a API Ninjas).
2.  Para traduzir campos da resposta (como nome e presas) de inglês para português (antes de exibi-los na tela de resultados).

Instruções para executar

Para compilar e executar este projeto, você precisará do Android Studio e de chaves de API válidas para os serviços utilizados.

1.  Clone este repositório para sua máquina local.
2.  Abra o projeto no Android Studio.
3.  Aguarde o Gradle sincronizar todas as dependências do projeto (definidas em `gradle/libs.versions.toml` e `app/build.gradle.kts`).
4. Configurar Chaves de API O projeto requer chaves para a Google Vision API e a API Ninjas. Você deve obter suas próprias chaves e substituí-las nos seguintes arquivos:
   Google Vision API Key Abra `app/src/main/java/com/example/animalinformationvisionai/MainActivity.java` e substitua a chave anexada à variável `url`.
   API Ninjas Key Abra `app/src/main/java/com/example/animalinformationvisionai/TelaResultado.java` e substitua o valor da variável `apiKey`.
5.  Conecte um dispositivo Android ou inicie um Emulador Android.
6.  Compile e execute o aplicativo.
7.  Certifique-se de que o dispositivo/emulador tenha permissões de câmera e armazenamento concedidas e acesso à internet.
