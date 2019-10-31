module.exports = function(pr) {
    return !(
        pr.source.branch.name.startsWith('release/') || 
        pr.source.branch.name.startsWith('hotfix/') ||
        pr.destination.branch.name != "develop"
    ) 
}