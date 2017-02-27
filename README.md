## Demo RP(Relying Party) with Nexsign on Bluemix

Demo RP is a demo RP application running on bluemix runtime demonstrating how to connect RPapplications to Nexsign service on Bluemix.

You can bind a Nexsign service instance to an Relying Party application running on Bluemix runtime and then use FIDO authentication with the Nexsign service.

[![Deploy to Bluemix](https://bluemix.net/deploy/button.png)](https://bluemix.net/deploy?repository=https://github.com/Nexsign/DemoRP)

### Running the app on Bluemix

Markdown is a lightweight and easy-to-use syntax for styling your writing. It includes conventions for

**1. If you do not already have a Bluemix account, sign up here**

**2. Download and install the Cloud Foundry CLI tool**

**3. Clone the app to your local environment from your terminal using the following command:**
```markdown
git clone https://github.com/Nexsign/Demo-RP.git
```

**4. ```cd``` into this newly created directory**

**5. Open the ```mainfest.yml``` file and change the ```host``` value to something unique.**

**6. Connect to Bluemix in the command line tool and follow the prompts to log in.**
```
$ cf api https://api.ng.bluemix.net
$ cf login
```

**7. Create a Demo RP service instance in Bluemix.**
```
$ cf create-service dashDB Entry dashDB-nodesample
```

**8. Push the app to Bluemix.**
```
$ cf push
```

You now have your very own instance of a Demo RP app running Bluemix runtime and connecting and using the Nexsign service on Bluemix.

### Related Links
**- [Samsung SDS](http://www.samsungsds.com/global/en/index.html)**

**- [Samsung SDS Nexsign](http://www.samsungsds.com/global/en/solutions/off/nexs/nexsign.html)**

