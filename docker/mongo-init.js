db = db.getSiblingDB('tukktak');

db.createUser({
  user: 'tukktak',
  pwd: 'tukktak',
  roles: [{ role: 'readWrite', db: 'tukktak' }]
});