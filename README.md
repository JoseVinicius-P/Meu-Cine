# Meu-Cine
O Aplicativo Meu Cine é um aplicativo Android desenvolvido com Java. Este app possui a simples funcionalidade de manter uma lista de filmes a gosto do usuário. A principal motivação para desenvolvimento deste app foi de caráter pessoal, pois eu gostaria de ter um app dedicado as este objetivo e até então fazia uso de uma lista de texto para colocar o nome dos filmes.
O carregamento dos filmes é feito consumindo os serviços da API gratuita do The Movie Data Base (TMDB) com a biblioteca retrofit, foi utilizada também a biblioteca Picasso para o carregamendo das imagens no geral, além disso foram usados componentes do SDK android como SharedPreferences (Para salvar lista do usuário) e RecyclerView (para exibição dos filmes)
A seguir está exposto um vídeo demonstrando o funcionamento do app:

<p align="center">
  <img width="200" src="Media Git/show_app.gif">
</p>

Este app se encontra em desenvolvimento, pois existem melhorias a serem feitas, como questões de segurança, de organização e implementação de algumas boas práticas de desenvolvimento. Em breve disponibilizaremos na Play Store.
Destacamos que nenhum tipo de dados do usuário são coletados e/ou transferidos, todos os dados referentes as listas são armazenados localmente no app
