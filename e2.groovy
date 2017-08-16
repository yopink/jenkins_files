import groovy.transform.Field;

//TODO a remplacer 
@Field def prefixUrlGit = "file:///home/2017/tmp/aa"

@Field def workDir = "E:/2016-2017/pro/outils/eclipse_ws/workspace"
@Field def versionList = [
	'cv_a1' : [branch : '' ],
	'cv_a2' : [branch : '' ],
	'cv_a3' : [branch : '' ],
	]
//def mvnHome = tool 'apache-maven-3.2.3'
//def jdkHome = tool 'java-8-oracle'

/*
for ( e in treeDeps ) {
	print "key = ${e.key}, value = ${e.value}"
}
*/
def processDeps(projetPom, deps){
	deps.each{ depsKey, depsVal ->
		println "projetPom : ${projetPom}, deps - depsKey--->${depsKey}, deps - depsVal---> ${depsVal}" 
		updateDep(projetPom, depsKey, depsVal)
	}
}

def updateDep(projetPom, depsKey , depsValue){	 
		println "projetPom : ${projetPom}, updateDep - ${depsKey}--->${depsValue}" 
		def newVal = getVersionFromDeps(depsKey)
		 
		 if(depsValue.type == "parent"){
			 println "\n====> replace de type : parent , projetPom : ${projetPom} , deps_a_remplace : ${depsKey}, newVal : ${newVal}"
			 replaceParentVersion(projetPom, newVal)
		 }
		 else if(depsValue.type == "exp"){

			 println "\n====>replace de type : exp , projetPom : ${projetPom} , deps_a_remplace : ${depsKey}, expression : ${depsValue.exp} , newVal : ${newVal}"
			 replaceExpression(projetPom, depsValue.exp, newVal)

		 }
//Commit
 dir("${projetPom}") {
	 println "\n===>${projetPom} : commit pom"
	     sh "git add ."
	     sh "git commit -m 'maj pom : ${depsValue.type} ${depsValue.exp}'  || echo 'No changes to commit' " 	
	} 



}

def replaceParentVersion(projetPom, newVal){
      dir("${projetPom}") {
	println "replaceParentVersion"
	def mvnHome = tool 'apache-maven-3.2.3'
 	//sh "${mvnHome}/bin/mvn versions:update-parent -DparentVersion=${newVal} -DallowSnapshots=true"
 	sh "echo 'Todo'"
 	
	}
}

def replaceExpression(projetPom, valuesToRepalce, newVal){
     dir("${projetPom}") {
    	println "replaceExpression"
	    sh   "sed -i \"s|<${valuesToRepalce}>.*</${valuesToRepalce}>|\\<${valuesToRepalce}>${newVal}\\</${valuesToRepalce}>|\" pom.xml"
     }
	}

def getVersionFromDeps(depsId){
if(versionList[depsId] != null){
	return versionList[depsId].version
}
	return null
}

def  extractVersionFromPom(project){
    	println "\n==>extractVersionFromPom : ${project}"
    	pom = readMavenPom file: 'pom.xml'
        def version = pom.version+"-"+new Date()
    	return version 
}

//Clone des depos
def coProjets(){ 
	versionList.each{ projectKey, projectVal ->
	    dir("${projectKey}") {
	       // git url: "${prefixUrlGit}/${projectKey}"

def gitUrl = "${prefixUrlGit}/${projectKey}"
checkout([
  $class: 'GitSCM', branches: [[name: '*/master']],
  userRemoteConfigs: [[url: gitUrl , depth  : 1   ]]
])


	    	println "projectKey : ${projectKey}, deps - projectVal--->${projectVal}" 
		    println "coProjets projectKey : ${projectKey}" 
		    projectVal.version = extractVersionFromPom(projectKey)
	    }
	}
}

def hello(){ 
println "hello"
}

return this