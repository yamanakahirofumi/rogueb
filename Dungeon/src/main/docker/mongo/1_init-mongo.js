const userName = 'mongouser';
const dbName = 'dungeon';

db = db.getSiblingDB('admin');
const users = db.getUsers({filter: {user: userName}});

if (users.length === 0) {
    const user = {
        user: userName,
        pwd: 'mongop@ss',
        roles: [{
            role: 'readWrite',
            db: dbName
        }]
    };
    db.createUser(user);
} else {
    const roles = users[0].roles;
    roles.push({role: 'readWrite', db: dbName});
    db.updateUser(userName, {roles: roles});
}