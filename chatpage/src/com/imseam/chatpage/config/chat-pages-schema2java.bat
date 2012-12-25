
set generate-dir=C:\workspace\imseam\chatpage\src
set package-name=com.imseam.chatpage.config.temp
set schema-path=%generate-dir%\com\imseam\chatpage\config\

xjc -d %generate-dir% -p %package-name% %schema-path%\chat-page.xsd