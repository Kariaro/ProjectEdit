const UPDATE_PROJECT_VERSION = {
  path: 'src/main/java/com/hardcoded/main/ProjectEdit.java',
  task: (contents, previousVersion, releaseVersion) => {
    return contents.replace(
      new RegExp(`VERSION = "${previousVersion}";`, 'g'),
      `VERSION = "${releaseVersion};";`
    );
  }
};

module.exports = {
  bumpFiles: [
    UPDATE_PROJECT_VERSION
  ]
};
