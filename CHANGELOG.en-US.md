# Changelog
All notable changes about agile-service will be documented in this file.

## [0.14.0] - 2019-02-22

### Added

- Warning hints for residence time of cards in board.
- Repeated prompt for sprint name.
- Board permission restriction.
- Support association when creating issues.
- Story point, remain time support decimal point after one

### Changed

- Show the completed issue and cross out the number.
- The stories which all the subtasks completed are sorted under the story swimming lane.
- Description optimization of active logs.
- Add or modify filter for issue links, issue management, version details, release versions, modules and quick filter.
- Under the epic swimming lane, the board supports viewing stories and reflects the relationship between stories and subtasks.
- Display optimization of task cards after filtering in different swimming lanes.     
- Optimized modification of logic problems related to creating modules.
- Return the creator's avatar and name when create a issue.
- The affected version can be selected to the full version.    
- Partial views style optimization. 
- Partial report optimization.


### Fixed

- Problems in pasting network pictures with sub-text.
- Repair the problem of double counting the number of burnout issues when closing the sprint.
- Issue is not refreshed synchronously in the backlog when modified the name of the epic.
- There is error when version or sprint is null in statistic chart.
- Work Calendar Non-Holiday Computing bug.
- Status color errors in story point statistics.


## [0.13.0] - 2019-01-11

### Added

- Version details filtering functionality.Version details support advanced filter.
- The statistical graph adds label dimensions, and adds sprint, version, and time filter conditions.
- The board name modifying functionality in board setting, and add the verification of duplicate name.
- Issue details narrow styles add log information.
- The issue is supported by filling in remaining time and story points when creating.
- Increase epic name, component name, and version name verification.

### Changed

- Select issues user action monitoring optimization in backlog view.
- Partial view style optimization.
- Optimize burndown chart calculation logic.

### Fixed

- The statistics of the sprinters in the planned backlog view are repeated.
- Cumulative flow graph dirty data repair, need to be manually called for repair.
- The creation state and the deletion state cause the state machine draft configuration table to generate dirty data and the publication is unavailable.
- The date selector's holiday display error.

## [0.12.0] - 2018-12-14

### Added

- Version details filtering functionality.Version details support advanced filter.
- Active sprint issues drag sort functionality.Issues support drag sort in active sprint's board view.
- Active sprint support filtering of team members.
- Work calendar added 2019 statutory holiday data.
- New issue link for version details.
- Version add the field of expected release date.

### Changed

- Issue creators can delete issues which they created.
- The state is automatically changed to the default state after the task is converted to a subtask.
- When the sprint is complete, the unfinished subtask moves with the parent task to the next sprint.
- Create a quick search of the relationship field to display the name changed to Chinese.
- The workload of the sprints in the backlog list is changed to the total number of issues, the remaining number of issues, the total task hours, and the remaining task hours.
- The field of end date changes to the field of expected release date when you create new version.
- User need to enter the actual release time when user release the version.
- The board sets the column constraint to be modified only by the project owner.
- User can only select the planning version to merger other version.
- Partial views style optimization.

### Fixed

- Statistics incomplete issues count error in version details view.
- The issue details priority drop-down list is incomplete.
- Failed to create version name in Chinese.

## [0.11.0] - 2018-11-16

### Added

- Issue type customization functionality.new issue-service, issue-service support custom issue type, support issue type icon customization, custom issue type will be applied in agile-service.
- Issue priority Customization functionality.The issue-service supports custom problem prioritization, and the custom issue priority will be applied in the agile service.
- Issue state machine functionality.new state-machine-service, issue status update, creation, and deletion are controlled by state-machine-service.

### Changed

- Issue management adds new field display, field search, field sorting.
- Issue management supports custom filtering.
- The Issue management view subtask is also displayed in the list.
- When the Issue in the active sprint is dragged to another location then the problem and its subtasks are all restored to the state machine initial state.
- Issue details form page optimization.
- Product global illustration optimization.
- Active sprint view display optimization.
- Backlog epic count details optimization.
- Calendar style and operation optimization.
- Calendar workdays and holidays return the current year and next year data by year.
- The issue link list shows the assigner message.
- The iteration speed graph is not counted the not start sprint.

### Fixed

- The data after the work log time registration in the issue management is not updated.
- The version, epic sorting error in backlog view.
- Active sprint and iteration work bench remaining time calculation error.
- The version report cache was not updated in a timely manner.
- Drag the issue in multiple states will show white screen in the active sprint.

## [0.10.5] - 2018-10-22

### Added

- In-site notification functionality.Users can assign corresponding notification objects to the issue creation,issue assignment and issue resolution at the organizational level.
- Time zone calendar functionality.Users can set time zones,holidays and workdays at the organizational level.It will be apply in agile-service.
- Version management search functionality.The version management list adds the field search functionality.
- Component management search functionality.The component management list adds the field search functionality.
- Sprint Workday functionality.Users can set the working day and non-working day of the current sprint when user start sprint and selecting date.
- Burndown chart expectation value workday screening functionality.Users can display the expected value on weekdays and non-working days when viewing the burndown chart.

### Changed

- Users can save the input data by pressing Enter or clicking on the blank space when modifying the issue information.
- Add a guided prompt page after the sprint create.
- Changes the icon of stories,tasks,epics,subtasks and bugs.
- Use the calendar setting for the remaining time of the active sprint.
- Active sprint switch table button style modification.
- Add a verification to the issue in the story map.
- Optimize creation issue request in the backlog list view.

### Fixed

- Users can create a issue link without entering a value in issue's detail.
- Users converted a issue to a subtask that the issue's status color is incorrect.
- The issue's details page component is not aligned.
- The release version page link to uncompleted issue list filter error.
- The link address Chinese has not been encoding to cause the request to be repeated.
- The subtask icon in the list of issues in the release version is incorrect.
- The no epic's issue card's style error in active sprint view.
- In burndown chart report view,user click on the subtask link is the parent task details.
- Link to issues management in the epic and version burndown chart, return to page 404.
- The table of iterative workbench sprint details loads all the data at one time, and repeatedly loads the data when the page is clicked.
- The number of uncompleted issues's counted errors when the version was released.

## [0.10.0] - 2018-09-24

### Added

- Epic burndown charts functionality.Users can select epic burndown charts in the report view. Charts and reports show the progress made by teams in different epics and predict the trend of future sprint completion.
- Version burndown charts functionality.Users can select version burndown charts in the report view. Charts and reports show the progress made by teams in different epics and predict the trend of future sprint completion.
- Sprint workbench functionality.Users can check the status, priority, assignee, and type distribution of the issues in the sprint workbench.
- Report workbench functionality.Users can view real-time data of the cumulative flow chart, assignee distribution chart and other charts in the report workbench.
- Story map export functionality.
- Full screen operation of story map functionality.

### Changed

- The sliding functionality of the story map can be more fluid.
- The story map can record its location when moving the issue.
- The story map can record its location when the requirements pool is dragging a issue.
- Partial view memory optimization.
- The burndown chart and sprint report in the report can build a cache to retain the last selected sprint and its units.
- Modify the style of adding state in the board configuration.
- Cumulative flow graph to obtain time function optimization.
- The version progress in the dashboard filters out the archived version.
- Creating issue links in the Settings adds duplicate validation.
- Story maps support drag up and down sorting in different swimlane.

### Fixed

- The page stack phenomenon occurs when user drag an issue to a column with multiple states in active sprint view.
- When user drag issue to sorting in active sprint view, data of this page will occurs delay problem.
- Creation issues take too long to execute in backlog view.
- Issue's descriptions is a string with formatting in Excel when user export Excel in issues manager view.
- The sprint burndown chart failed under some conditions which according to the number of issues.

### Removed

- The paging functionality does not show the paging toolbar if it is less than 10.


## [0.9.5] - 2018-08-31

### Added

- Dashboard functionality.Users can customize the dashboard in the home page.The dashboard contains: burndown chart, version progress, epic progress, my unfinished issues and more.
- User story map functionality.User story map is based on epic, planning and managing issues according to version and sprint dimensions.
- User story map swim lane functionality.Users can select none, version, sprint to divide the swim lane and record the user's choice of swimlane.
- User story map demand pool functionality.Users can filter all unallocated epic's issues in the demand pool of the user story map.
- User story map issue drag functionality. Users can drag the issue between different epics, versions, sprints, or drag to the map board in the demand pool.
- Report chart caching functionality: Cache the charts in the report via Redis.
- The issues in the chart can transfer to issue management view (without subtasks).
- Unit tests for backlog, active sprints, release version and component management.
- Record the swim lane settings of the user's active sprint corresponding to the board.
- Issue management view export Excel contains subtasks.

### Changed

- Some view styles are modified.
- Partial view memory optimization.
- Cumulative flow chart query optimization.

### Fixed

- Drag issue repeatedly generates a data log in the backlog view.
- The backlog view has no issue cause sprint data does not receive.
- Dragging issues to an unsupported version failed in the backlog view.
- Dragging issues to an unsupported version failed in the backlog view.
- When there are column constraints of the board configuration in the active sprint view, the constraints can be skipped directly by modifying the state of the backlog view.
- Failed to delete the version in the version management view.

## [0.9.0] - 2018-08-17

### Added

- Version drag sort functionality.Users can drag the version to sort versions in the version management view and the backlog view.
- Epic drag sort functionality.Users can drag the epic to sort epics in the backlog view.
- Quick Search sort functionality.Users can drag the quick search and sort quick searchs in the set-up quick search view.
- Sprint speed chart functionality.Users can select story points, issues counts,remaining time to see issues's resolve and unresolved proportional histogram of different sprints.
- Epic report functionality.Users can select different epics through story points, issues counts, remaining time to see the current sprint resolve, unresolved, unresolved and unpredictable issues.And users also can see the corresponding summary data.
- Issue statistics chart functionality.Users can view the issue statistics chart in the project according to the assignee, component, issue type, fix version, priority, status, sprint, epic and resolve results.
- Issue's details back to history view functionality.Users can back to the original page after clicking on the issue's details in any views.
- Added agile-service unit testing based on Spock.
- Added create branch functionality in the issue's details operation.
- When users modify the state to completed and the issue resolve log will be generated.
- Modify sprint name have length limit.

### Changed

- Agile-service message propagation is modified from Kafka to Saga.
- Optimize the request time of the version report.
- Optimize the equest time of the burndown report.
- View style adjustment for backlog view.
- Optimize the problem of slow loading of epics and versions in the backlog view.
- Data logs processing logic refactoring.
- Modify version status style.

### Fixed
- Memory overflow for backlog view.
- Burndown chart data are inconsistent.
- Cumulative flow chart data are inconsistent.
- Data display is inconsistent after component management creation component.

### Removed
- Details of the issue are displayed on the left side of the narrow work log and activity log.
- Cumulative flow graph statistics for epics and their sub tasks.

## [0.8.0] - 2018-07-20

### Added

- Branch management functionality.Users can creating,merging and viewing branch information from Gitlab remote repository branches associated with issues in issue detail view.
- Version report functionality.The version report shows your team's progress in completing the version. The version report can be filtered based on the remaining estimated time, story points, and issue counts. The version report will also be based on your team's since the release. The average progress (speed) and the estimated remaining workload show you the predicted release date.
- Cumulative flow diagram functionality.The cumulative flow diagram is an area graph showing the status of various work items for the application, version and sprint. The horizontal x-axis represents time, the vertical y-axis represents the problem count. Each colored area of the chart is equivalent to the issue change listed on the panel, and the cumulative flow diagram can be used to identify bottlenecks.If your chart contains areas that are vertically widened over time.A column equal to the widened area usually becomes a bottleneck.
- Test type issue functionality.Test type issue are used in the "Test Manager" module, from which users can create test type issue for managing test cases.
- Project default setting functionality.The project owner can set the default assignee and the default priority of the project's issues. If user create issue havn't assignee or priority.System will create the default assignee and priority according to the project settings. The default priority of the project is lower than the default assignee of the component.
- User default board functionality.If user selects the board,system will recordeing what your select.When the user enter  active sprint view,the board selected by the user will be displayed.
- Issues export Excel functionality.Users can filter out issues according to the selected conditions then export to the table.
- Issue converted to subtask functionality.Users can convert other types of issues into subtasks.In particular, the story issue is converted to a subtask.And the story point is modified to 0.
- Issue copy functionality.Users can copy the issue by selecting parameters:question link and subtask.Copy the issue will generate a copy type link with the original issue.
- The release log can be viewed in the version view.
- Version log export Markdown document functionality.Users can export issues informations to Markdown document in the release version of the version log.

### Changed

- The epic type issues defaults to the initial color modification.
- During update the version association of the issue, you cannot delete the version association that has been archived.
- Optimize the search interface and modify the trigger logic.
- Optimize the burndown chart data query interface.
- The release time display field is modified from the start time to the release time.
- Hidden no lanes are assigned when no issues on the board.
- The issue card on the board can view epic information.
- Modify the active sprint first in the menu order.
- Modify the location of the epic name in the issue details.
- Issue details style optimization.
- Backlog list view style optimization.
- Remove the project code duplicate name check in the project settings.
- Issues is arranged to refresh each time in a certain order on the board.
- When you select issues in the backlog list, if you click on the details of one of the issue, you can select multiple issues based on the issue.
- The project creates an initial test type issue.
- Add the action of converted to subtask, copy operation in the issue details.
- Release issues can be linked to issues management by clicking on the link.
- The report view can be associated with a list of issues and details of each issue.
- The report view adds a version report, cumulative flow diagram entry.
- Report selects switches add the version report and the cumulative flowchart.
- The issues in the sprint report can be viewed by grouping in the report to the issues management view.
- The swim lane type in the board settings is added to display issue.

### Fixed
- Issue details Anchor location is not accurate.
- The epic color in the issue details does not match the epic color.
- When the issue is based on the story display and selecting only my issues.The parent task does not belong to the same assignee's swim lane display defect.
- Select the board style question.
- Active sprint story point statistics color error.
- A Easy way to create issues with Caton.
- The filter user limits 20 people when the filter is created.
- The issue details select the manager and reporter component issue.
- You can only select up to 400 issues when selecting a link issue.
- Creationing issue link by search result failed.
- The backlog list issues is dragged into the version but the list of issues in the corresponding version is not refreshed in real time.
- The list shows the data operation load delay.
- The backlog list issues is dragged to the sprint but the sprint's users information is not updated.
- When the issue summary is switched during editing, the contents of the edit box will be cleared.
- The problem is dragged into the version but not create logs.
- New project creation issue but issue number starting from 2.
- Issue Management quickly creates epics but epic havn't epic name.
- The rich text editor failed to break words in the case of multiple English.
- The story issue changed to other types but the issue's story points not set to 0,also when  the epic type issue changed to other types then the issues that belongs to the epic was not updated.
- The board in Firefox browser shows a style error.
- The board's swim lane error  when the epic issues does not exist.