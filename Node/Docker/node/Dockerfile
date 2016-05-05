FROM java:8

COPY node.jar ./

EXPOSE 53452

if [ "$CONTEXT" ]; then
	CMD java -jar node.jar "$MANAGER_URL" "-Dhro.inflab.dockyou.node.container.ContainerContext=$CONTEXT"
else
	CMD java -jar node.jar "$MANAGER_URL"
fi