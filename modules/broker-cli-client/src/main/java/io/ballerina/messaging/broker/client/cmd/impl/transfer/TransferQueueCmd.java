package io.ballerina.messaging.broker.client.cmd.impl.transfer;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import io.ballerina.messaging.broker.client.cmd.impl.grant.GrantCmd;
import io.ballerina.messaging.broker.client.http.HttpClient;
import io.ballerina.messaging.broker.client.http.HttpRequest;
import io.ballerina.messaging.broker.client.http.HttpResponse;
import io.ballerina.messaging.broker.client.output.ResponseFormatter;
import io.ballerina.messaging.broker.client.resources.Configuration;
import io.ballerina.messaging.broker.client.resources.Message;
import io.ballerina.messaging.broker.client.utils.Constants;
import io.ballerina.messaging.broker.client.utils.Utils;

import java.net.HttpURLConnection;

import static io.ballerina.messaging.broker.client.utils.Constants.BROKER_ERROR_MSG;
import static io.ballerina.messaging.broker.client.utils.Constants.HTTP_PUT;

/**
 * Command representing MB queue ownership transferring.
 */
@Parameters(commandDescription = "Transfer ownership of a queue in the Broker")
public class TransferQueueCmd extends GrantCmd {

    @Parameter(description = "name of the queue",
               required = true)
    private String queueName;

    @Parameter(names = { "--new-owner", "-n" },
               description = "user id of the new owner",
               required = true)
    private String newOwner;

    public TransferQueueCmd(String rootCommand) {
        super(rootCommand);
    }

    @Override
    public void execute() {
        if (help) {
            processHelpLogs();
            return;
        }

        Configuration configuration = Utils.getConfiguration(password);
        HttpClient httpClient = new HttpClient(configuration);
        HttpRequest httpRequest = new HttpRequest(
                Constants.QUEUES_URL_PARAM + queueName + Constants.PERMISSIONS_OWNER_URL_PARAM,
                getJsonRequestPayload());

        // do DELETE
        HttpResponse response = httpClient.sendHttpRequest(httpRequest, HTTP_PUT);

        // handle response
        if (response.getStatusCode() == HttpURLConnection.HTTP_OK) {
            Message message = buildResponseMessage(response, "Queue ownership transferred successfully");
            ResponseFormatter.printMessage(message);
        } else {
            ResponseFormatter.handleErrorResponse(buildResponseMessage(response, BROKER_ERROR_MSG));
        }

    }

    private String getJsonRequestPayload() {
        return "{\"owner\":\"" + newOwner + "\"}";
    }

    @Override
    public void appendUsage(StringBuilder out) {
        out.append("Usage:\n");
        out.append("  " + rootCommand + " revoke queue [queue-name]\n");
    }
}