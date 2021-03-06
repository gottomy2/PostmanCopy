package edu.pjatk.postman.controller.request;

import edu.pjatk.postman.controller.request.model.GetRequestResponse;
import edu.pjatk.postman.controller.request.model.GetRequestsResponses;
import edu.pjatk.postman.controller.request.model.PostRequestRequest;
import edu.pjatk.postman.controller.request.model.PutRequestRequest;
import edu.pjatk.postman.repository.model.Param;
import edu.pjatk.postman.repository.model.Request;
import edu.pjatk.postman.repository.model.User;
import edu.pjatk.postman.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author Igor Motowidło (gottomy2)
 * Complete API for request entities
 */

@RestController
@RequestMapping("/request")
public class RequestController {
    private final RequestService requestService;
    private final ParamService paramService;
    private final UserService userService;

    @Autowired
    public RequestController(RequestService requestService, ParamService paramService, UserService userService) {
        this.requestService = requestService;
        this.paramService=paramService;
        this.userService=userService;
    }

    /**
     * Finds Request by its id in the database.
     * @param id - id of the request to find
     * @return STATUS CODE: 200 on success with Request within body | STATUS CODE: 404 on failure.
     */
    @GetMapping("/getRequestById/{requestId}")
    public ResponseEntity<GetRequestResponse> getRequest(@PathVariable("requestId") Long id) {
        Optional<Request> request = requestService.findRequestById(id);
        return request.map(value -> ResponseEntity.ok(new GetRequestResponse(value.getId(),value.getUserId(),value.getHost(),value.getUrl(),value.getType())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Finds ids of all requests in the database and returns a list of Longs
     * @return List of all Long ids from database
     */
    @GetMapping("/getAllIds")
    public GetRequestsResponses getAllRequestsIds(){
        return new GetRequestsResponses(requestService.findAllRequests());
    }

    //TODO: solve "type=Method Not Allowed, status=405" problem.
    /**
     * Creates new Request entity in the database
     * @param requestRequest new request parameters values declared in body.
     * @return STATUS CODE: 404 on failure | STATUS CODE: 201 on success with API call of getRequest() method.
     */
    @PostMapping("/createRequest")
    public ResponseEntity<Void> createRequest(@RequestBody PostRequestRequest requestRequest){
        Optional<User> checkUser = userService.findUserById(requestRequest.getUserId());

        if(checkUser.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        Request request = new Request(requestRequest.getUserId(),requestRequest.getHost(),requestRequest.getUrl(),requestRequest.getType());
        System.out.println(request);
        requestService.createRequest(request);
        return ResponseEntity.created(URI.create("http://localhost:9090/request/getRequestById/"+request.getId())).build();
    }

    /**
     * Updates existing request in the database
     * @param putRequestRequest request entity declared in body of Put REQUEST
     * @return STATUS CODE: 404 on non existing entity | STATUS CODE: 200 on success
     */
    @PutMapping("/updateRequest")
    public ResponseEntity<Void> updateRequest(@RequestBody PutRequestRequest putRequestRequest){
        System.out.println(putRequestRequest.getId());
        Optional<Request> request = requestService.findRequestById(putRequestRequest.getId());
        Optional<User> checkUser = userService.findUserById(putRequestRequest.getUserId());

        if(request.isEmpty() || checkUser.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        request.get().setId(putRequestRequest.getId());
        request.get().setUrl(putRequestRequest.getUrl());
        request.get().setUserId(putRequestRequest.getUserId());
        requestService.updateRequest(request.get());
        return ResponseEntity.ok().build();
    }

    /**
     * Removes existing Request entity from the database
     * @param id id of request to remove
     * @return STATUS CODE: 404 if entity does not exist | STATUS CODE: 200 on success
     */
    @DeleteMapping("/deleteRequest/{requestId}")
    public ResponseEntity<Void> deleteRequest(@PathVariable("requestId") Long id){
        Optional<Request> request = requestService.findRequestById(id);
        if(request.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        else{
            requestService.deleteRequest(request.get());
            List<Param> param = paramService.getParamsByRequestId(id);
            if(!param.isEmpty()){
               paramService.deleteParam(param.get(0));
            }

            return ResponseEntity.ok().build();
        }
    }
}
