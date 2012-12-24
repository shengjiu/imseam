
set generate-dir=..\src
set package-name=com.imseam.chatlet.config
set schema-path=..\src\com\imseam\chatlet\config

xjc -d %generate-dir% -p %package-name% %schema-path%\chatlet-config.xsd