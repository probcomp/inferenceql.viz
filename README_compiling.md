# Compiling iql.viz

### Configure GitHub SSH keys

Building InferenceQL involves fetching private repositories from GitHub over SSH. If you have not set up SSH to be able to connect to GitHub follow [these instructions](https://docs.github.com/en/github/authenticating-to-github/connecting-to-github-with-ssh). If you have already configured SSH to connect to GitHub [test your SSH connection to GitHub](https://docs.github.com/en/github/authenticating-to-github/testing-your-ssh-connection).

Due to a bug upstream the InferenceQL build process _must_ use `ssh-agent` to access your SSH keys. To ensure that this is happens you must must remove any configuration settings that direct SSH to use files instead. Print out your SSH settings by running `cat ~/.ssh/config`. You may see an `IdentityFile` declaration, like this:

```
Host *
  AddKeysToAgent yes
  UseKeychain yes
  IdentityFile ~/.ssh/id_rsa
```

The line `IdentityFile ~/.ssh/id_rsa` tells SSH to use the key file `~/.ssh/id_rsa` when accessing the referenced host.

For each `IdentityFile` line first verify that the referenced key is already present in `ssh-agent`. For security reasons `ssh-agent` uses unique character sequences called "fingerprints" to uniquely identify keys it is managing. Run `ssh-keygen -l -f` on any files for which there is a `IdentityFile` declaration and make note of the fingerprint. The `*` portion is the fingerprint.

```
% ssh-keygen -l -f ~/.ssh/id_rsa
4096 SHA256:****************************************** user@host.com (RSA)
```

Next list the SSH keys `ssh-agent` is managing by running `ssh-add -l`. Verify that each of the fingerprints printed by `ssh-keygen -l -f` matches one of the fingerprints displayed by `ssh-add -l`. If any are missing [add them to the SSH agent](https://docs.github.com/en/github/authenticating-to-github/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent#adding-your-ssh-key-to-the-ssh-agent).

```
% ssh-add -l
4096 SHA256:****************************************** /Users/user/.ssh/id_rsa (RSA)
```

Next comment out the `IdentityFile` lines in `~/.ssh/config` by prepending them with `#`:

```
Host *
  AddKeysToAgent yes
  UseKeychain yes
  # IdentityFile ~/.ssh/id_rsa
```

Finally [test your SSH connection to GitHub](https://docs.github.com/en/github/authenticating-to-github/testing-your-ssh-connection) once more.

### Get dependencies

This assumes you are on Mac OSX.

There are a few dependencies. Hopefully, you have homebrew installed. You can check by just running `brew` in the terminal. If you have it installed, you should get some output. If not, you can download and install it here.

https://brew.sh/

After homebrew is installed, run the following in the terminal.

```
brew install clojure
brew install yarn
```

Those are all the dependencies you need.

### Compile the app

In the project root directory, compile the project with the following.

```
make clean
make
```

### Open the app

You can then open the app by running `open index.html` at the command line or by double clicking the `index.html` file.

### Create a JS-bundle  (optional)

After it is done, you can pluck things from the project directory to form your own js bundle without all the unnecessary source files. This is optional, as you can already open the app just fine with out this step.

In the `inferenceql/` dir you will find the `index.html` file, the `resources` dir, and the compiled `out` dir. All three go together to form the compiled javascript app. However, the `out` dir was the only thing compiled when you ran `make`.

Just bundle these three things together, and you will have your own js-bundle.

# Using the iql.viz with different data and models

* First you will need to produce a new model file using a BayesDB notebook.
* Then you will re-compile InferenceQL with your new data file and new model file included. 

**(Note: this last step is no longer necessary. You can upload your new model via the change dataset/model panel within the app.)**

## Create a new model file using a BayesDB notebook

### Install docker

To check if Docker is already installed and running, run:
 ```
 docker version
 ```
If that command returns an error, you'll need to install Docker.

First obtain Docker (for [Mac and Windows](https://www.docker.com/products/docker-desktop), for [Linux](https://docs.docker.com/install/linux/docker-ce/ubuntu/)).

If on Linux, make sure to run the [post-installation steps](https://docs.docker.com/install/linux/linux-postinstall/) so that you can run the Docker commands smoothly without needing sudo access.

If on Mac, run the Docker application, and make sure that you see the
Docker whale icon in your menu bar (when you click on the icon it
should say "Docker Desktop is running").

### Run the BayesDB docker image

Download the docker image. (Image is ~10GB. This may take a while.)
```
docker pull probcomp/notebook:edge
```

Run the docker image.
```
docker run -p 8888:8888 probcomp/notebook:edge
```

Open the url printed in the terminal in a web browser. It should look something like
```
http://localhost:8888/?token=some-long-token.....
```

### Upload a notebook file and CSV file for model building

Upload a notebook file (.ipynb) and a data file (.csv) into the notebook file tree––perhaps in a new folder.

Each of the included demos has a notebook file and csv in their respective directories. The notebook file for gapminder-africa is more detailed however. Simply open up the notebook file and follow the instructions for model building for that demo.

See this picture for help finding the upload button in the notebook interface.

https://drive.google.com/file/d/1rB8Qko6F-iHJfcNJzbgZfG7X1CYJVepg/view

### Download your model file and CSV file

There are instructions at the end of each notebook for taking the model produced by BayesDB and brining it into InferenceQL for use with the spreadsheets app.

Here is a re-print of those instructions.

The last command run in your notebook should be `%dump_models data` where data is the name of the BayesDB population you are working with. This will produce a `data_models.json` file in the same directory as the notebook.

Download `data_models.json` and the original csv that you used to produce the model.

### Copy your model file and csv file over to the InferenceQL source tree

Copy both of the model file and csv file to `inferenceql/resources` directory within the InferenceQL source tree. Be sure to rename your csv file as `data.csv`. You will be replacing the existing `data_models.json` and `data.csv` files.

Now you are ready to compile the app with your new model and dataset.
