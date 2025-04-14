pipeline {
    // Defining Agents
    agent any
    environment {
        DOCKERHUB_USERNAME = "hsinemt"
        BACKTAG = "${DOCKERHUB_USERNAME}/devopsproject:backapp"
    }

    // Defining Stages
    stages {
        stage('Checkout - Backend') {
            steps {
                script {
                    // Stage 1: Git Checkout
                    checkout([$class: 'GitSCM',
                        branches: [[name: '*/HsineMtiraoui-4Twin1-G1']],  // Match any branch with "Back" in its name
                        userRemoteConfigs: [[
                            url: 'https://github.com/firasbessalah/devopsProjet.git'
                        ]]
                    ])
                }
            }
        }
        stage('Cleaning Project') {
            steps {
                script {
                    // Stage 2: Compile the project into a .jar file
                    sh "mvn clean install"
                }
            }
        }
        stage('Backend Compilation') {
            steps {
                script {
                    // Stage 2: Compile the project into a .jar file
                    sh "mvn compile"
                }
            }
        }


        // Unitary Test
        stage('Unitary Tests') {
            steps {
                script {
                    // Stage 3: Run tests
                    sh "mvn test"
                }
            }
        }


        // Build

        stage('Building Backend Application') {
            steps {
                script {
                    // Stage 4: Build the application
                    sh "mvn package"
                }
            }
        }


        //Sonar
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "Running SonarQube analysis"
                    withSonarQubeEnv(credentialsId: 'SonarToken') {
                        sh "mvn sonar:sonar"
                    }
                }
            }
        }





        //Nexus

        stage('Nexus - Deploying Artifacts') {
            steps {
                script {
                    // Execute mvn deploy skipping tests
                    sh "mvn deploy -DskipTests" //-U
                }
            }
        }

        //Docker

        // Building Docker Image for Backend
        stage('Docker Image Build - Backend') {
            steps {
                script {
                    sh "docker build -t $BACKTAG ."
                }
            }
        }
        stage('Docker Login Backend') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'DockerHubCreds', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh "docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD"
                    }
                }
            }
        }
        stage('Docker Push - Backend Application') {
            steps {
                script {
                    sh "docker push $BACKTAG"
                }
            }
        }


         // Deploying Docker Compose
        stage('Docker Compose - Deployment') {
            steps {
                sh 'docker compose up -d'  // Deploy Docker Compose services
            }
        }

        // Sending Email Notification (NgRok + Cron Job)
        stage('Email Alerting Notification') {
            steps {
                script {
                    // Reading the contents of the README.md file
                    def contenuReadMe = readFile('README.md')
                    def subject = 'New DevOps Project Pipeline Commit - Hsine Mtiraoui'
                    def buildStartTime = new Date(currentBuild.startTimeInMillis)
                    def formattedDate = buildStartTime.format('yyyy-MM-dd HH:mm:ss')
                    def body = "A new commit has been made to the repository on ${formattedDate}.\n\n${contenuReadMe}"
                    def to = 'hsinemt1899@gmail.com'
                    mail(
                        subject: subject,
                        body: body,
                        to: to,
                    )
                }
            }
        }
    }
    post {
        success {
            emailext subject: 'Successful Deployment',
                      body: 'Your pipeline was successfully deployed.',
                      to: 'hsinemt1899@gmail.com'
        }
        failure {
            emailext subject: 'Deployment Failed',
                      body: 'Your pipeline was not successfully deployed. Verify file logs at /var/log/jenkins.log for more information.',
                      to: 'hsinemt1899@gmail.com'
        }
    }

    }
