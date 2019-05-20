import faker from "faker";
import _ from "lodash";

const numPeople = 50;
const numSessions = 20;
const numCompanies = 10;
const numFlights = 80;
const numTopics = 8;

const getRandomInt = () => _.sample([3, 4, 5, 6, 7, 8]);

// build companies
const companies = [];
for (let i = 0; i < numCompanies; i++) {
  companies.push(faker.company.companyName());
}

// build sessions
const sessions = [];
for (let i = 0; i < numSessions; i++) {
  sessions.push({
    group: "nodes",
    data: {
      nodeType: "session",
      label: "",
      id: "session-" + faker.random.uuid(),
      name: faker.random.words()
    }
  });
}

// build topics
const topics = [];
for (let i = 0; i < numTopics; i++) {
  topics.push({
    group: "nodes",
    data: {
      nodeType: "topic",
      label: "",
      id: "topic-" + faker.random.uuid(),
      name: faker.random.word()
    }
  });
}

// buildFlights
const flights = [];
for (let i = 0; i < numFlights; i++) {
  flights.push({
    group: "nodes",
    data: {
      nodeType: "flight",
      label: "",
      id: "flight-" + faker.random.uuid(),
      duration: faker.random.number()
    }
  });
}

// build people
const people = [];
for (let i = 0; i < numPeople; i++) {
  people.push({
    group: "nodes",
    data: {
      nodeType: "person",
      label: "",
      id: "person-" + faker.random.uuid(),
      name: faker.name.findName(),
      email: faker.internet.email(),
      company: _.sample(companies),
      bestTime: faker.random.number()
    }
  });
}

// link people to people
const worksWithEdges = [];
people.forEach(person => {
  const coworkers = _.filter(people, ["data.company", person.data.company]);
  coworkers.forEach(coWorker => {
    if (coWorker.data.id !== person.data.id) {
      worksWithEdges.push({
        group: "edges",
        classes: "worksWithEdge",
        data: {
          label: "works with",
          id: faker.random.uuid(),
          source: person.data.id,
          target: coWorker.data.id
        }
      });
    }
  });
});

// link people flights
const flewEdges = [];
people.forEach((person, index) => {
  flewEdges.push({
    group: "edges",
    classes: "flewEdges",
    data: {
      label: "flew",
      id: faker.random.uuid(),
      source: person.data.id,
      target: flights[index].data.id
    }
  });
});

// link the rest of the flights randomly
for (var i = numPeople; i < numFlights; i++) {
  flewEdges.push({
    group: "edges",
    classes: "flewEdges",
    data: {
      label: "flew",
      id: faker.random.uuid(),
      source: _.sample(people).data.id,
      target: flights[i].data.id
    }
  });
}

// link people to sessions
const attendedEdges = [];
people.forEach(person => {
  const randomSessions = _.sampleSize(sessions, getRandomInt());
  randomSessions.forEach(session => {
    attendedEdges.push({
      classes: "attendedEdges",
      group: "edges",
      data: {
        label: "attended",
        id: faker.random.uuid(),
        source: person.data.id,
        target: session.data.id
      }
    });
  });
});

// link people to topics
const interestedEdges = [];
people.forEach(person => {
  const randomTopics = _.sampleSize(topics, getRandomInt());
  randomTopics.forEach(topic => {
    interestedEdges.push({
      classes: "interestedEdges",
      group: "edges",
      data: {
        label: "interested in",
        id: faker.random.uuid(),
        source: person.data.id,
        target: topic.data.id
      }
    });
  });
});

const graphData = [
  ...sessions,
  ...topics,
  ...people,
  ...flights,
  ...worksWithEdges,
  ...flewEdges,
  ...attendedEdges,
  ...interestedEdges
];

// build races
const numRaces = 20;
const races = [];
for (let i = 0; i < numRaces; i++) {
  races.push({
    id: faker.random.uuid(),
    racer: faker.name.findName(),
    type: _.sample(["Completed", "Crashed", "Cancelled"]),
    start_time: faker.random.number(),
    end_time: faker.random.number()
  });
}

export const tableData = [...races];

const generatePersonNeighborhood = () => {
  // create person
  const person = {
    group: "nodes",
    data: {
      nodeType: "person",
      label: "",
      id: "person-" + faker.random.uuid(),
      name: faker.name.findName(),
      email: faker.internet.email(),
      company: _.sample(companies),
      bestTime: faker.random.number()
    }
  };

  // create flight
  const flight = {
    group: "nodes",
    data: {
      nodeType: "flight",
      label: "",
      id: "flight-" + faker.random.uuid(),
      duration: faker.random.number()
    }
  };

  // create a flew edge
  const flewEdge = {
    group: "edges",
    classes: "flewEdges",
    data: {
      label: "flew",
      id: faker.random.uuid(),
      source: person.data.id,
      target: flight.data.id
    }
  };

  //create topic edges
  const interestedEdges = [];
  const randomTopics = _.sampleSize(topics, getRandomInt());
  randomTopics.forEach(topic => {
    interestedEdges.push({
      classes: "interestedEdges",
      group: "edges",
      data: {
        label: "interested in",
        id: faker.random.uuid(),
        source: person.data.id,
        target: topic.data.id
      }
    });
  });

  // create works with edges
  const worksWithEdges = [];
  const coworkers = _.filter(timeSeriesGraphData, [
    "data.company",
    person.data.company
  ]);
  coworkers.forEach(coWorker => {
    if (coWorker.data.id !== person.data.id) {
      worksWithEdges.push({
        group: "edges",
        classes: "worksWithEdge",
        data: {
          label: "works with",
          id: faker.random.uuid(),
          source: person.data.id,
          target: coWorker.data.id
        }
      });
    }
  });

  // create session edges
  const randomSessions = _.sampleSize(sessions, getRandomInt());
  const attendedEdges = [];
  randomSessions.forEach(session => {
    attendedEdges.push({
      classes: "attendedEdges",
      group: "edges",
      data: {
        label: "attended",
        id: faker.random.uuid(),
        source: person.data.id,
        target: session.data.id
      }
    });
  });

  return [
    person,
    flight,
    flewEdge,
    ...attendedEdges,
    ...interestedEdges,
    ...worksWithEdges
  ];
};

let firstRun = true;
let timeSeriesGraphData = [...sessions, ...topics];
export const getMockGraphData = () => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      if (!firstRun) {
        timeSeriesGraphData = [
          ...timeSeriesGraphData,
          ...generatePersonNeighborhood()
        ];
      }
      firstRun = false;
      resolve(timeSeriesGraphData);
    }, 2000);
  });
};
